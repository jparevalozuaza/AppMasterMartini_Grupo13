package cl.duoc.mastermartini.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.database.AppDatabase
import cl.duoc.mastermartini.data.repository.PreferenciasRepository
import cl.duoc.mastermartini.data.repository.UsuarioRepository
import cl.duoc.mastermartini.model.PerfilUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

// ─────────────────────────────────────────────────────────────
// PERFILVIEWMODEL: perfil (Room), preferencias (DataStore) y foto (galería)
// ─────────────────────────────────────────────────────────────
class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository =
        UsuarioRepository(AppDatabase.getDatabase(application).usuarioDao())
    private val preferencias = PreferenciasRepository(application)

    private val _estado = MutableStateFlow(PerfilUiState())
    val estado: StateFlow<PerfilUiState> = _estado.asStateFlow()

    // Preferencias de accesibilidad: se leen de DataStore y se exponen como StateFlow
    val textoGrande: StateFlow<Boolean> =
        preferencias.textoGrande.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val reducirMovimiento: StateFlow<Boolean> =
        preferencias.reducirMovimiento.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val subtitulos: StateFlow<Boolean> =
        preferencias.subtitulos.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Carga el usuario de la sesión actual (correo guardado en DataStore → datos en Room)
    fun cargarPerfil() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val email = preferencias.emailSesion.first()
            val usuario = if (email != null) usuarioRepository.buscarPorEmail(email) else null
            _estado.update { it.copy(cargando = false, usuario = usuario, editando = false) }
        }
    }

    // ===================== EDICIÓN =====================

    fun iniciarEdicion() {
        val usuario = _estado.value.usuario ?: return
        _estado.update {
            it.copy(
                editando = true,
                nombre = usuario.nombre,
                telefono = usuario.telefono,
                direccion = usuario.direccion,
                nombreError = null,
                telefonoError = null
            )
        }
    }

    fun cancelarEdicion() {
        _estado.update { it.copy(editando = false) }
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, nombreError = null) }
    }

    fun onTelefonoChange(valor: String) {
        _estado.update { it.copy(telefono = valor, telefonoError = null) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor) }
    }

    // Reutiliza las mismas validaciones del Registro (Validation.kt)
    fun guardarCambios() {
        val actual = _estado.value
        val usuario = actual.usuario ?: return
        val nombreError = validarNombre(actual.nombre)
        val telefonoError = validarTelefono(actual.telefono)

        if (nombreError != null || telefonoError != null) {
            _estado.update { it.copy(nombreError = nombreError, telefonoError = telefonoError) }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            val actualizado = usuario.copy(
                nombre = actual.nombre.trim(),
                telefono = actual.telefono.trim(),
                direccion = actual.direccion.trim()
            )
            usuarioRepository.actualizar(actualizado)
            _estado.update {
                it.copy(guardando = false, editando = false, usuario = actualizado, mensaje = "Perfil actualizado")
            }
        }
    }

    // ===================== RECURSO NATIVO: GALERÍA =====================

    // Recibe la imagen elegida en la galería (su "Uri") y la guarda como foto de perfil.
    fun guardarFotoDesdeGaleria(uri: Uri) {
        val usuario = _estado.value.usuario ?: return
        viewModelScope.launch {
            _estado.update { it.copy(guardandoFoto = true) }
            val ruta = withContext(Dispatchers.IO) { copiarImagenAlAlmacenamientoInterno(uri, usuario.id) }
            if (ruta == null) {
                _estado.update {
                    it.copy(guardandoFoto = false, mensaje = "No se pudo cargar la imagen. Prueba con otra.")
                }
                return@launch
            }
            usuario.fotoPath?.let { File(it).delete() }   // borra la foto anterior
            val actualizado = usuario.copy(fotoPath = ruta)
            usuarioRepository.actualizar(actualizado)
            _estado.update { it.copy(guardandoFoto = false, usuario = actualizado, mensaje = "Foto actualizada") }
        }
    }

    // SEGURIDAD: la galería solo da permiso TEMPORAL para leer la imagen elegida.
    // Por eso se copia una versión reducida al almacenamiento INTERNO de la app (privado),
    // y en Room se guarda la ruta de esa copia.
    private fun copiarImagenAlAlmacenamientoInterno(uri: Uri, usuarioId: Int): String? {
        return try {
            val app = getApplication<Application>()
            val resolver = app.contentResolver

            // 1) Leer solo el tamaño de la imagen, sin cargarla completa en memoria
            val tamano = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, tamano) }

            // 2) Calcular cuánto reducirla (las fotos de celular pueden pesar varios MB)
            var reduccion = 1
            while (tamano.outWidth / (reduccion * 2) >= 512 && tamano.outHeight / (reduccion * 2) >= 512) {
                reduccion *= 2
            }

            // 3) Cargar la imagen ya reducida
            val opciones = BitmapFactory.Options().apply { inSampleSize = reduccion }
            val imagen = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
                ?: return null

            // 4) Guardarla como JPG en la carpeta privada de la app
            val archivo = File(app.filesDir, "foto_perfil_${usuarioId}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(archivo).use { salida ->
                imagen.compress(Bitmap.CompressFormat.JPEG, 85, salida)
            }
            archivo.absolutePath
        } catch (e: Exception) {
            null   // archivo dañado o formato no soportado: se informa al usuario
        }
    }

    // ===================== PREFERENCIAS =====================

    fun setTextoGrande(valor: Boolean) {
        viewModelScope.launch { preferencias.setTextoGrande(valor) }
    }

    fun setReducirMovimiento(valor: Boolean) {
        viewModelScope.launch { preferencias.setReducirMovimiento(valor) }
    }

    fun setSubtitulos(valor: Boolean) {
        viewModelScope.launch { preferencias.setSubtitulos(valor) }
    }

    // ===================== MENSAJES Y SESIÓN =====================

    fun mostrarMensaje(mensaje: String) {
        _estado.update { it.copy(mensaje = mensaje) }
    }

    fun consumirMensaje() {
        _estado.update { it.copy(mensaje = null) }
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            preferencias.cerrarSesion()
            _estado.value = PerfilUiState()
            onListo()
        }
    }
}

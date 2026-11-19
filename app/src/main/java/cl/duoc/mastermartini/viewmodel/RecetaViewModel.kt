package cl.duoc.mastermartini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.repository.ProveedorRecetas
import cl.duoc.mastermartini.data.repository.RecetaRepository
import cl.duoc.mastermartini.model.Receta
import cl.duoc.mastermartini.ui.state.RecetaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// ─────────────────────────────────────────────────────────────
// VIEWMODEL DE RECETAS (misma estructura que PostViewModel de la Guía 14)
// Mantiene el estado de las pantallas de Recetas y ejecuta el CRUD
// a través del repositorio.
//
// El repositorio llega como PARÁMETRO (con valor por defecto):
//   • la app usa el que entrega ProveedorRecetas (simulado o remoto);
//   • las pruebas del Bloque 5 pueden entregarle uno falso.
// Como todos los parámetros tienen valor por defecto, Kotlin genera un
// constructor vacío y viewModel() en MainActivity lo sigue creando igual.
// ─────────────────────────────────────────────────────────────
class RecetaViewModel(
    private val repository: RecetaRepository = ProveedorRecetas.repositorio()
) : ViewModel() {

    // Estado principal: Cargando, Exito (con la lista) o Error.
    // "_estado" es privado y modificable; "estado" es público y de solo lectura.
    private val _estado = MutableStateFlow<RecetaUiState>(RecetaUiState.Cargando)
    val estado: StateFlow<RecetaUiState> = _estado.asStateFlow()

    // Atajo: solo la lista de recetas (vacía mientras carga o si hay error)
    val recetas: StateFlow<List<Receta>> = estado
        .map { (it as? RecetaUiState.Exito)?.recetas ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Mensaje breve tras crear, editar o eliminar (null = sin mensaje).
    // Lo mostrará un Snackbar en el Bloque 3.
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // Se ejecuta automáticamente al crear el ViewModel
    init {
        cargarRecetas()
    }

    // ===================== CONSULTAR (GET) =====================
    fun cargarRecetas() {
        viewModelScope.launch {                       // corrutina atada a la vida del ViewModel
            _estado.value = RecetaUiState.Cargando
            _estado.value = try {
                RecetaUiState.Exito(repository.obtenerRecetas())
            } catch (e: Exception) {
                RecetaUiState.Error(mensajeDeError(e))
            }
        }
    }

    // ===================== CREAR (POST) =====================
    fun crearReceta(receta: Receta) {
        viewModelScope.launch {
            try {
                repository.crearReceta(receta.copy(id = 0))   // id 0 = nueva
                _mensaje.value = "Receta creada"
                refrescarLista()
            } catch (e: Exception) {
                _mensaje.value = mensajeDeError(e)
            }
        }
    }

    // ===================== ACTUALIZAR (PUT) =====================
    fun actualizarReceta(receta: Receta) {
        viewModelScope.launch {
            try {
                val actualizada = repository.actualizarReceta(receta)
                _mensaje.value =
                    if (actualizada != null) "Receta actualizada" else "La receta ya no existe"
                refrescarLista()
            } catch (e: Exception) {
                _mensaje.value = mensajeDeError(e)
            }
        }
    }

    // ===================== ELIMINAR (DELETE) =====================
    fun eliminarReceta(id: Int) {
        viewModelScope.launch {
            try {
                val eliminada = repository.eliminarReceta(id)
                _mensaje.value =
                    if (eliminada) "Receta eliminada" else "La receta ya no existía"
                refrescarLista()
            } catch (e: Exception) {
                _mensaje.value = mensajeDeError(e)
            }
        }
    }

    // La pantalla avisa que ya mostró el mensaje
    fun consumirMensaje() {
        _mensaje.value = null
    }

    // Vuelve a pedir la lista sin mostrar "Cargando", para que la pantalla
    // refleje lo que quedó guardado después de una operación
    private suspend fun refrescarLista() {
        try {
            _estado.value = RecetaUiState.Exito(repository.obtenerRecetas())
        } catch (e: Exception) {
            _estado.value = RecetaUiState.Error(mensajeDeError(e))
        }
    }

    // Traduce una excepción técnica a un mensaje que entiende el usuario
    private fun mensajeDeError(e: Exception): String = when (e) {
        is IOException ->                             // sin red, servidor apagado, tiempo agotado
            "No se pudo conectar con el servidor. Revisa tu conexión e inténtalo de nuevo."
        is HttpException ->                           // el servidor respondió con un código de error
            "El servidor respondió con un error (código ${e.code()})."
        else ->
            "Ocurrió un error inesperado. Inténtalo de nuevo."
    }
}

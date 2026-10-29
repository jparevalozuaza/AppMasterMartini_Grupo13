package cl.duoc.mastermartini.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.database.AppDatabase
import cl.duoc.mastermartini.data.entity.UsuarioEntity
import cl.duoc.mastermartini.data.preferences.PreferenciasRepository
import cl.duoc.mastermartini.data.repository.UsuarioRepository
import cl.duoc.mastermartini.model.LoginUiState
import cl.duoc.mastermartini.model.RegistroErrores
import cl.duoc.mastermartini.model.RegistroUiState
import cl.duoc.mastermartini.validation.validarClaveLogin
import cl.duoc.mastermartini.validation.validarClaveNueva
import cl.duoc.mastermartini.validation.validarEmail
import cl.duoc.mastermartini.validation.validarIntereses
import cl.duoc.mastermartini.validation.validarNombre
import cl.duoc.mastermartini.validation.validarRut
import cl.duoc.mastermartini.validation.validarSeleccion
import cl.duoc.mastermartini.validation.validarTelefono
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
// AUTHVIEWMODEL: lógica de Login y Registro (guía sección 8.2)
// Flujo: evento de la UI → ViewModel → validación/repositorio → nuevo UiState
// AndroidViewModel = ViewModel que recibe "application" para crear Room y DataStore.
// ─────────────────────────────────────────────────────────────
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository =
        UsuarioRepository(AppDatabase.getDatabase(application).usuarioDao())
    private val preferencias = PreferenciasRepository(application)

    // Estado del Login
    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    // Estado del Registro
    private val _registro = MutableStateFlow(RegistroUiState())
    val registro: StateFlow<RegistroUiState> = _registro.asStateFlow()

    init {
        // Asegura que exista la cuenta demo (demo@martini.cl / martini123)
        viewModelScope.launch { usuarioRepository.crearUsuarioDemoSiNoExiste() }
    }

    // ===================== LOGIN =====================

    fun onLoginEmailChange(valor: String) {
        _login.update { it.copy(email = valor, emailError = null, loginError = null) }
    }

    fun onLoginClaveChange(valor: String) {
        _login.update { it.copy(clave = valor, claveError = null, loginError = null) }
    }

    // onExito: lo que la pantalla quiere hacer si el login funciona (navegar a Inicio)
    fun iniciarSesion(onExito: () -> Unit) {
        val actual = _login.value
        val emailError = validarEmail(actual.email)
        val claveError = validarClaveLogin(actual.clave)

        if (emailError != null || claveError != null) {
            _login.update { it.copy(emailError = emailError, claveError = claveError) }
            return
        }

        viewModelScope.launch {
            _login.update { it.copy(cargando = true, loginError = null) }
            val usuario = usuarioRepository.validarCredenciales(actual.email, actual.clave)
            if (usuario != null) {
                preferencias.guardarSesion(usuario.email)
                _login.value = LoginUiState()   // limpia el formulario
                onExito()
            } else {
                _login.update {
                    it.copy(
                        cargando = false,
                        loginError = "Correo o contraseña incorrectos. Revisa los datos e inténtalo de nuevo."
                    )
                }
            }
        }
    }

    // ===================== REGISTRO =====================
    // Cada función actualiza un campo y borra el error de ese campo

    fun onNombreChange(valor: String) {
        _registro.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onEmailChange(valor: String) {
        _registro.update { it.copy(email = valor, errores = it.errores.copy(email = null)) }
    }

    fun onTelefonoChange(valor: String) {
        _registro.update { it.copy(telefono = valor, errores = it.errores.copy(telefono = null)) }
    }

    fun onRutChange(valor: String) {
        _registro.update { it.copy(rut = valor, errores = it.errores.copy(rut = null)) }
    }

    fun onClaveChange(valor: String) {
        _registro.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onTipoNegocioChange(valor: String) {
        _registro.update { it.copy(tipoNegocio = valor, errores = it.errores.copy(tipoNegocio = null)) }
    }

    fun onTamanoNegocioChange(valor: String) {
        _registro.update { it.copy(tamanoNegocio = valor, errores = it.errores.copy(tamanoNegocio = null)) }
    }

    fun onDireccionChange(valor: String) {
        _registro.update { it.copy(direccion = valor) }
    }

    // Selección múltiple: si el interés ya estaba, se quita; si no, se agrega
    fun onInteresClick(interes: String) {
        _registro.update {
            val nuevos = if (interes in it.intereses) it.intereses - interes else it.intereses + interes
            it.copy(intereses = nuevos, errores = it.errores.copy(intereses = null))
        }
    }

    fun consumirMensajeRegistro() {
        _registro.update { it.copy(mensaje = null) }
    }

    fun registrar(onExito: () -> Unit) {
        val actual = _registro.value
        val errores = RegistroErrores(
            nombre = validarNombre(actual.nombre),
            email = validarEmail(actual.email),
            telefono = validarTelefono(actual.telefono),
            rut = validarRut(actual.rut),
            clave = validarClaveNueva(actual.clave),
            tipoNegocio = validarSeleccion(actual.tipoNegocio, "el tipo de negocio"),
            tamanoNegocio = validarSeleccion(actual.tamanoNegocio, "el tamaño del negocio"),
            intereses = validarIntereses(actual.intereses)
        )

        if (errores.hayErrores()) {
            _registro.update { it.copy(errores = errores, mensaje = "Revisa los campos marcados en rojo") }
            return
        }

        viewModelScope.launch {
            _registro.update { it.copy(guardando = true) }

            if (usuarioRepository.existeEmail(actual.email)) {
                _registro.update {
                    it.copy(guardando = false, errores = it.errores.copy(email = "Este correo ya está registrado"))
                }
                return@launch
            }

            val nuevoUsuario = UsuarioEntity(
                nombre = actual.nombre.trim(),
                email = actual.email.trim().lowercase(),
                telefono = actual.telefono.trim(),
                rut = actual.rut.trim(),
                tipoNegocio = actual.tipoNegocio,
                tamanoNegocio = actual.tamanoNegocio,
                direccion = actual.direccion.trim(),
                intereses = actual.intereses.joinToString(","),
                claveHash = UsuarioRepository.hashClave(actual.clave)
            )
            usuarioRepository.registrar(nuevoUsuario)
            preferencias.guardarSesion(nuevoUsuario.email)
            _registro.value = RegistroUiState()   // limpia el formulario
            onExito()
        }
    }
}

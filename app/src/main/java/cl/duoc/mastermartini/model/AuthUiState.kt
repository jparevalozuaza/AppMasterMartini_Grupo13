package cl.duoc.mastermartini.model

// ─────────────────────────────────────────────────────────────
// ESTADOS DE LA UI PARA LOGIN Y REGISTRO
// "UiState" = una foto de todo lo que la pantalla necesita dibujar.
// La pantalla solo lee estos datos; el ViewModel es quien los cambia.
// ─────────────────────────────────────────────────────────────

data class LoginUiState(
    val email: String = "",
    val clave: String = "",
    val emailError: String? = null,
    val claveError: String? = null,
    val loginError: String? = null,   // error general: "correo o contraseña incorrectos"
    val cargando: Boolean = false,
    val ingresando: Boolean = false   // true = login correcto: se muestra la barra de carga animada
) {
    // El botón "Ingresar" se habilita solo si hay datos y no se está procesando el ingreso
    val puedeIngresar: Boolean
        get() = email.isNotBlank() && clave.isNotBlank() && !cargando && !ingresando
}

data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val rut: String = "",
    val clave: String = "",
    val tipoNegocio: String = "",
    val tamanoNegocio: String = "",
    val direccion: String = "",
    val intereses: Set<String> = emptySet(),
    val errores: RegistroErrores = RegistroErrores(),
    val guardando: Boolean = false,
    val mensaje: String? = null       // mensaje breve para mostrar en Snackbar
)

// Un posible error por cada campo (null = sin error)
data class RegistroErrores(
    val nombre: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val rut: String? = null,
    val clave: String? = null,
    val tipoNegocio: String? = null,
    val tamanoNegocio: String? = null,
    val intereses: String? = null
) {
    fun hayErrores(): Boolean =
        listOf(nombre, email, telefono, rut, clave, tipoNegocio, tamanoNegocio, intereses)
            .any { it != null }
}

// Opciones fijas que se muestran como chips en el Registro
object OpcionesRegistro {
    val tiposNegocio = listOf("Panadería", "Pastelería", "Chocolatería", "Heladería", "Restaurante", "Casino", "Otro")
    val tamanos = listOf("Pequeño", "Mediano", "Grande")
    val intereses = listOf("Panadería", "Pastelería", "Chocolatería", "Heladería", "Bollería", "Masas de hoja", "Galletas", "Empanadas")
}

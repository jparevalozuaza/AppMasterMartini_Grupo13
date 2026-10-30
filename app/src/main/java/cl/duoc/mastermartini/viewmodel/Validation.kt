package cl.duoc.mastermartini.viewmodel

// ─────────────────────────────────────────────────────────────
// VALIDACIONES REUTILIZABLES (guía 8 pantallas, sección 7.2)
// Cada función recibe un dato y devuelve:
//   null   → el dato es válido
//   String → el mensaje de error que verá el usuario
// Están en el paquete viewmodel porque son lógica de presentación:
// las usan AuthViewModel y PerfilViewModel (un solo lugar, sin repetir código).
// No dependen de Compose.
// ─────────────────────────────────────────────────────────────

private val REGEX_EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

fun validarNombre(nombre: String): String? {
    val limpio = nombre.trim()
    return when {
        limpio.isEmpty() -> "Ingresa tu nombre completo"
        limpio.length < 2 -> "El nombre debe tener al menos 2 caracteres"
        limpio.length > 60 -> "El nombre no puede superar 60 caracteres"
        else -> null
    }
}

fun validarEmail(email: String): String? {
    val limpio = email.trim()
    return when {
        limpio.isEmpty() -> "Ingresa tu correo electrónico"
        !REGEX_EMAIL.matches(limpio) -> "Correo no válido. Ejemplo: nombre@correo.cl"
        else -> null
    }
}

// En el Login solo se exige que la contraseña no esté vacía
fun validarClaveLogin(clave: String): String? =
    if (clave.isBlank()) "Ingresa tu contraseña" else null

// Al crear cuenta se exige un mínimo de 6 caracteres
fun validarClaveNueva(clave: String): String? = when {
    clave.isBlank() -> "Crea una contraseña"
    clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
    else -> null
}

// Acepta celulares chilenos: 9 dígitos (9XXXXXXXX) o con código país (569XXXXXXXX)
fun validarTelefono(telefono: String): String? {
    val digitos = telefono.filter { it.isDigit() }
    return when {
        digitos.isEmpty() -> "Ingresa tu teléfono"
        digitos.length == 9 && digitos.startsWith("9") -> null
        digitos.length == 11 && digitos.startsWith("569") -> null
        else -> "Teléfono no válido. Ejemplo: +56 9 5555 1234"
    }
}

// RUT opcional: si viene vacío es válido; si viene, se revisa el dígito verificador (módulo 11)
fun validarRut(rut: String): String? {
    if (rut.isBlank()) return null
    val limpio = rut.replace(".", "").replace("-", "").replace(" ", "").uppercase()
    if (limpio.length < 2) return "RUT no válido"
    val cuerpo = limpio.dropLast(1)
    val dv = limpio.last()
    if (!cuerpo.all { it.isDigit() }) return "RUT no válido. Ejemplo: 12.345.678-5"

    var suma = 0
    var multiplicador = 2
    for (digito in cuerpo.reversed()) {
        suma += digito.digitToInt() * multiplicador
        multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
    }
    val resultado = 11 - (suma % 11)
    val dvEsperado = when (resultado) {
        11 -> '0'
        10 -> 'K'
        else -> resultado.digitToChar()
    }
    return if (dv == dvEsperado) null else "RUT no válido: revisa el dígito verificador"
}

// Para opciones que se eligen con chips (tipo y tamaño de negocio)
fun validarSeleccion(valor: String, campo: String): String? =
    if (valor.isBlank()) "Selecciona $campo" else null

fun validarIntereses(intereses: Set<String>): String? =
    if (intereses.isEmpty()) "Elige al menos un interés" else null

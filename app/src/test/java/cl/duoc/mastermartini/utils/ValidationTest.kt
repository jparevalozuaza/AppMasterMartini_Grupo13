package cl.duoc.mastermartini.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE LAS VALIDACIONES DE LOGIN, REGISTRO Y PERFIL (Validation.kt)
// Son la lógica que usan AuthViewModel y PerfilViewModel en sus formularios.
// ─────────────────────────────────────────────────────────────
class ValidationTest : StringSpec({

    "el correo debe tener formato válido" {
        validarEmail("").shouldNotBeNull()
        validarEmail("demo@martini").shouldNotBeNull()
        validarEmail("demo@martini.cl").shouldBeNull()
    }

    "la contraseña nueva exige al menos 6 caracteres; en el login solo no estar vacía" {
        validarClaveNueva("12345").shouldNotBeNull()
        validarClaveNueva("martini123").shouldBeNull()
        validarClaveLogin("").shouldNotBeNull()
        validarClaveLogin("x").shouldBeNull()
    }

    "el teléfono acepta celulares chilenos con o sin +56" {
        validarTelefono("12345").shouldNotBeNull()
        validarTelefono("955551234").shouldBeNull()
        validarTelefono("+56 9 5555 1234").shouldBeNull()
    }

    "el RUT revisa el dígito verificador y es opcional" {
        validarRut("").shouldBeNull()                  // opcional
        validarRut("12.345.678-5").shouldBeNull()      // dígito correcto
        validarRut("12.345.678-9").shouldNotBeNull()   // dígito incorrecto
    }
})

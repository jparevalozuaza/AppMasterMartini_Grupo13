package cl.duoc.mastermartini.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE LOS ROLES: solo los correos de la lista son administradores
// ─────────────────────────────────────────────────────────────
class RolesTest : StringSpec({

    "la cuenta demo es administradora, sin importar mayúsculas ni espacios" {
        Roles.esAdministrador("demo@martini.cl") shouldBe true
        Roles.esAdministrador(" Demo@Martini.cl ") shouldBe true
    }

    "un usuario común o una sesión vacía no son administradores" {
        Roles.esAdministrador("cliente@martini.cl") shouldBe false
        Roles.esAdministrador(null) shouldBe false
    }
})

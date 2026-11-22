package cl.duoc.mastermartini.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE LAS VALIDACIONES DEL FORMULARIO DE RECETAS
// Regla: null = válido; texto = mensaje de error.
// ─────────────────────────────────────────────────────────────
class ValidacionRecetaTest : StringSpec({

    "el título es obligatorio, de al menos 3 y como máximo 60 caracteres" {
        validarTituloReceta("").shouldNotBeNull()
        validarTituloReceta("Pa").shouldNotBeNull()
        validarTituloReceta("a".repeat(61)).shouldNotBeNull()
        validarTituloReceta("Pan amasado").shouldBeNull()
    }

    "el tiempo debe ser un número entre 1 y 1440 minutos" {
        validarTiempoPreparacion("").shouldNotBeNull()
        validarTiempoPreparacion("0").shouldNotBeNull()
        validarTiempoPreparacion("1500").shouldNotBeNull()
        validarTiempoPreparacion("45").shouldBeNull()
    }

    "ingredientes y preparación respetan el máximo de 255 caracteres de MySQL" {
        validarTextoLargoReceta("Corto", "los ingredientes").shouldNotBeNull()
        validarTextoLargoReceta("a".repeat(256), "los ingredientes").shouldNotBeNull()
        validarTextoLargoReceta("Harina, agua tibia y sal", "los ingredientes").shouldBeNull()
    }

    "la categoría y la descripción son obligatorias" {
        validarCategoriaReceta(" ").shouldNotBeNull()
        validarCategoriaReceta("Pastelería").shouldBeNull()
        validarDescripcionReceta("Corta").shouldNotBeNull()
        validarDescripcionReceta("Brownie húmedo de chocolate").shouldBeNull()
    }
})

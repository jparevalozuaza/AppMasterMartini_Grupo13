package cl.duoc.mastermartini.ui.state

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE LOS ESTADOS DE PANTALLA (UiState)
// Solo se prueban las partes que tienen lógica: propiedades calculadas
// (puedeIngresar, esEdicion, hayErrores) y las conversiones del formulario.
// ─────────────────────────────────────────────────────────────
class EstadosUiTest : StringSpec({

    "el botón Ingresar se habilita solo con correo y clave, y no mientras se procesa" {
        LoginUiState().puedeIngresar shouldBe false

        val conDatos = LoginUiState(email = "demo@martini.cl", clave = "martini123")
        conDatos.puedeIngresar shouldBe true
        conDatos.copy(cargando = true).puedeIngresar shouldBe false
        conDatos.copy(ingresando = true).puedeIngresar shouldBe false
    }

    "RegistroErrores detecta si queda al menos un campo con error" {
        RegistroErrores().hayErrores() shouldBe false
        RegistroErrores(intereses = "Elige al menos un interés").hayErrores() shouldBe true
    }

    "el formulario de recetas distingue crear de editar y limpia los textos al convertirse" {
        RecetaFormUiState().esEdicion shouldBe false

        val formulario = RecetaFormUiState(
            id = 7, titulo = "  Kuchen de nuez  ", categoria = "Pastelería", tiempoPreparacion = "abc",
            descripcion = "Kuchen del sur", ingredientes = "Nueces", preparacion = "Hornear"
        )
        formulario.esEdicion shouldBe true

        val receta = formulario.aReceta()
        receta.titulo shouldBe "Kuchen de nuez"   // sin espacios sobrantes
        receta.tiempoPreparacion shouldBe 0       // un texto no numérico se vuelve 0

        // Y de vuelta: la receta se puede abrir otra vez en el formulario
        receta.aFormulario().tiempoPreparacion shouldBe "0"
        receta.aFormulario().id shouldBe 7
    }

    "el formulario de la API externa distingue POST (nueva) de PUT (edición)" {
        RecetaExternaFormUiState().esEdicion shouldBe false
        RecetaExternaFormUiState(id = 3).esEdicion shouldBe true
    }
})

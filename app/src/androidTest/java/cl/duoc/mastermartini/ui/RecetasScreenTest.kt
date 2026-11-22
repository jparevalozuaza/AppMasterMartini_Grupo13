package cl.duoc.mastermartini.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.compose.rememberNavController
import cl.duoc.mastermartini.data.repository.RecetaFakeRepository
import cl.duoc.mastermartini.ui.screens.RecetaFormScreen
import cl.duoc.mastermartini.ui.screens.RecetasScreen
import cl.duoc.mastermartini.viewmodel.RecetaViewModel
import org.junit.Rule
import org.junit.Test

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE INTERFAZ (JUnit + Compose UI Test, Guía 15 Parte 5)
// Se ejecutan en el emulador: dibujan la pantalla real y la revisan
// buscando textos y botones, como lo haría una persona.
// Usan el repositorio simulado (sin pausa) para no depender del servidor.
// ─────────────────────────────────────────────────────────────
class RecetasScreenTest {

    // Regla que prepara una pantalla de Compose vacía para cada prueba
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun viewModelConDatosSimulados() =
        RecetaViewModel(RecetaFakeRepository(demoraMs = 0))

    // Espera (máximo 5 s) a que aparezca un texto, porque la carga es asíncrona
    private fun esperarTexto(texto: String) {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun laListaMuestraElTituloYLasRecetas() {
        composeTestRule.setContent {
            RecetasScreen(
                viewModel = viewModelConDatosSimulados(),
                navController = rememberNavController(),
                esAdministrador = true
            )
        }
        esperarTexto("Brownie de chocolate")

        composeTestRule.onNodeWithText("Recetas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Brownie de chocolate").assertIsDisplayed()
    }

    @Test
    fun unAdministradorVeElBotonCrear() {
        composeTestRule.setContent {
            RecetasScreen(
                viewModel = viewModelConDatosSimulados(),
                navController = rememberNavController(),
                esAdministrador = true
            )
        }

        composeTestRule.onNodeWithContentDescription("Crear receta").assertIsDisplayed()
    }

    @Test
    fun unUsuarioComunNoVeElBotonCrear() {
        composeTestRule.setContent {
            RecetasScreen(
                viewModel = viewModelConDatosSimulados(),
                navController = rememberNavController(),
                esAdministrador = false
            )
        }
        esperarTexto("Brownie de chocolate")

        composeTestRule.onNodeWithContentDescription("Crear receta").assertDoesNotExist()
    }

    @Test
    fun elFormularioVacioMuestraLosErroresAlGuardar() {
        composeTestRule.setContent {
            RecetaFormScreen(
                recetaId = 0,
                viewModel = viewModelConDatosSimulados(),
                onVolver = {}
            )
        }

        // El botón está al final del formulario: primero se desplaza hasta él
        composeTestRule.onNodeWithText("Crear receta").performScrollTo().performClick()

        // El mensaje de error aparece bajo el campo Título (más arriba en la pantalla)
        composeTestRule.onNodeWithText("Ingresa el título de la receta").assertExists()
    }
}

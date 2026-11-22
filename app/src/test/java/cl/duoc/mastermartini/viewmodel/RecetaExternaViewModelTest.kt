package cl.duoc.mastermartini.viewmodel

import cl.duoc.mastermartini.data.repository.RecetaExternaRepository
import cl.duoc.mastermartini.model.RecetaExterna
import cl.duoc.mastermartini.ui.state.RecetaExternaUiState
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.IOException

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE RecetaExternaViewModel (API externa DummyJSON)
// Misma estructura que RecetaViewModelTest: repositorio simulado con MockK.
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalCoroutinesApi::class)
class RecetaExternaViewModelTest : StringSpec({

    val pizza = RecetaExterna(1, "Margherita Pizza", "Italian", "Easy", 20,
        ingredientes = listOf("Dough", "Tomato"), instrucciones = listOf("Bake"))
    val tacos = RecetaExterna(2, "Tacos", "Mexican", "Easy", 15, ingredientes = listOf("Tortilla"))

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun repoConLista(): RecetaExternaRepository {
        val repo = mockk<RecetaExternaRepository>()
        coEvery { repo.obtenerRecetas() } returns listOf(pizza, tacos)
        return repo
    }

    "al crearse hace GET y muestra las recetas de la API" {
        val vm = RecetaExternaViewModel(repoConLista())

        vm.estado.value.shouldBeInstanceOf<RecetaExternaUiState.Exito>().recetas shouldBe listOf(pizza, tacos)
    }

    "una búsqueda sin resultados queda en Exito con la lista vacía (respuesta vacía)" {
        val repo = repoConLista()
        coEvery { repo.buscarRecetas("zzzz") } returns emptyList()
        val vm = RecetaExternaViewModel(repo)

        vm.onBusquedaChange("zzzz")
        vm.buscar()

        vm.estado.value.shouldBeInstanceOf<RecetaExternaUiState.Exito>().recetas.shouldBeEmpty()
    }

    "sin Internet el estado es Error con un mensaje claro" {
        val repo = mockk<RecetaExternaRepository>()
        coEvery { repo.obtenerRecetas() } throws IOException("sin red")

        val vm = RecetaExternaViewModel(repo)

        vm.estado.value.shouldBeInstanceOf<RecetaExternaUiState.Error>()
            .mensaje shouldStartWith "No se pudo conectar con la API externa"
    }

    "crear (POST) agrega la receta arriba, marcada como creada en la app" {
        val repo = repoConLista()
        coEvery { repo.crearReceta(any()) } answers { firstArg<RecetaExterna>().copy(id = 51) }
        val vm = RecetaExternaViewModel(repo)

        vm.abrirFormularioNueva()
        vm.onNombreChange("Pastel de choclo")
        vm.onCocinaChange("Chilean")
        vm.onMinutosChange("60")
        vm.onIngredientesChange("Choclo, carne, pollo")
        vm.guardarFormulario()

        val lista = vm.estado.value.shouldBeInstanceOf<RecetaExternaUiState.Exito>().recetas
        lista.size shouldBe 3
        lista.first().nombre shouldBe "Pastel de choclo"
        lista.first().ingredientes shouldBe listOf("Choclo", "carne", "pollo")
        lista.first().soloLocal shouldBe true
        vm.mensaje.value.shouldNotBeNull() shouldContain "POST exitoso"
        vm.formulario.value.visible shouldBe false
    }

    "editar (PUT) una receta que DummyJSON no conoce (404) la cambia solo en la app" {
        val repo = repoConLista()
        coEvery { repo.actualizarReceta(any()) } returns null      // 404
        val vm = RecetaExternaViewModel(repo)

        vm.abrirFormularioEdicion(tacos)
        vm.onNombreChange("Tacos al pastor")
        vm.guardarFormulario()

        coVerify { repo.actualizarReceta(match { it.id == 2 }) }
        val lista = vm.estado.value.shouldBeInstanceOf<RecetaExternaUiState.Exito>().recetas
        lista.first { it.id == 2 }.nombre shouldBe "Tacos al pastor"
        vm.mensaje.value.shouldNotBeNull() shouldContain "solo en la app"
    }

    "eliminar (DELETE) quita la receta de la lista y confirma la operación" {
        val repo = repoConLista()
        coEvery { repo.eliminarReceta(1) } returns true
        val vm = RecetaExternaViewModel(repo)

        vm.eliminarReceta(pizza)

        vm.estado.value.shouldBeInstanceOf<RecetaExternaUiState.Exito>().recetas shouldBe listOf(tacos)
        vm.mensaje.value.shouldNotBeNull() shouldContain "DELETE exitoso"
    }
})

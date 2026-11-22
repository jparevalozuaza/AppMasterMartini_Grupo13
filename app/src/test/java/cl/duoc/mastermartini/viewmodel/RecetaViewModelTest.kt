package cl.duoc.mastermartini.viewmodel

import cl.duoc.mastermartini.data.repository.RecetaRepository
import cl.duoc.mastermartini.model.Receta
import cl.duoc.mastermartini.ui.state.RecetaUiState
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
// PRUEBAS DE RecetaViewModel (Kotest + MockK)
// El repositorio se reemplaza por un MOCK: un objeto simulado al que le
// decimos qué responder (coEvery) y al que le preguntamos qué recibió (coVerify).
// Así se prueba SOLO la lógica del ViewModel, sin servidor ni base de datos.
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalCoroutinesApi::class)
class RecetaViewModelTest : StringSpec({

    // Datos de ejemplo que "devuelve" el repositorio simulado
    val brownie = Receta(1, "Brownie", "Brownie húmedo de chocolate", "Pastelería", 45,
        "Chocolate, mantequilla y huevos", "Mezclar todo y hornear 30 minutos")
    val pan = Receta(2, "Pan amasado", "Pan casero tradicional chileno", "Panadería", 90,
        "Harina, agua, sal y levadura", "Amasar, reposar y hornear")

    // viewModelScope usa el hilo principal de Android, que no existe en estas pruebas:
    // antes de cada prueba se reemplaza por un dispatcher de prueba, y después se restaura
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    // Crea el ViewModel con un repositorio simulado que ya sabe responder la lista
    fun crearViewModel(repo: RecetaRepository = mockk()): Pair<RecetaViewModel, RecetaRepository> {
        coEvery { repo.obtenerRecetas() } returns listOf(brownie, pan)
        return RecetaViewModel(repo) to repo
    }

    "al crearse, carga las recetas y queda en estado Exito con la lista" {
        val (vm, _) = crearViewModel()

        val estado = vm.estado.value.shouldBeInstanceOf<RecetaUiState.Exito>()
        estado.recetas shouldBe listOf(brownie, pan)
        vm.recetas.value.size shouldBe 2
    }

    "si no hay conexión, el estado es Error con un mensaje para el usuario" {
        val repo = mockk<RecetaRepository>()
        coEvery { repo.obtenerRecetas() } throws IOException("sin red")

        val vm = RecetaViewModel(repo)

        val estado = vm.estado.value.shouldBeInstanceOf<RecetaUiState.Error>()
        estado.mensaje shouldStartWith "No se pudo conectar con el servidor"
    }

    "cargarSiHayError vuelve a pedir las recetas después de un error" {
        val repo = mockk<RecetaRepository>()
        coEvery { repo.obtenerRecetas() } throws IOException("sin red")
        val vm = RecetaViewModel(repo)

        coEvery { repo.obtenerRecetas() } returns listOf(brownie)   // ahora el servidor responde
        vm.cargarSiHayError()

        vm.estado.value.shouldBeInstanceOf<RecetaUiState.Exito>().recetas shouldBe listOf(brownie)
    }

    "un formulario vacío no se guarda y marca el error de cada campo" {
        val (vm, repo) = crearViewModel()
        vm.prepararFormulario(0)
        var volvio = false

        vm.guardarFormulario { volvio = true }

        val f = vm.formulario.value
        f.tituloError.shouldNotBeNull()
        f.categoriaError.shouldNotBeNull()
        f.tiempoPreparacionError.shouldNotBeNull()
        f.preparacionError.shouldNotBeNull()
        volvio shouldBe false
        coVerify(exactly = 0) { repo.crearReceta(any()) }
    }

    "al escribir en un campo se borra su error y el tiempo solo acepta dígitos" {
        val (vm, _) = crearViewModel()
        vm.prepararFormulario(0)
        vm.validarFormulario()

        vm.onTituloChange("Kuchen")
        vm.onTiempoPreparacionChange("4a5b")

        vm.formulario.value.tituloError.shouldBeNull()
        vm.formulario.value.tiempoPreparacion shouldBe "45"
    }

    "guardar una receta nueva llama a crearReceta, deja el mensaje y vuelve atrás" {
        val (vm, repo) = crearViewModel()
        coEvery { repo.crearReceta(any()) } answers { firstArg<Receta>().copy(id = 3) }
        vm.prepararFormulario(0)
        vm.onTituloChange("Kuchen de nuez")
        vm.onCategoriaChange("Pastelería")
        vm.onTiempoPreparacionChange("60")
        vm.onDescripcionChange("Kuchen tradicional del sur")
        vm.onIngredientesChange("Harina, nueces, azúcar y huevos")
        vm.onPreparacionChange("Mezclar, hornear y servir frío")
        var volvio = false

        vm.guardarFormulario { volvio = true }

        coVerify(exactly = 1) { repo.crearReceta(match { it.id == 0 && it.titulo == "Kuchen de nuez" }) }
        vm.mensaje.value shouldBe "Receta creada"
        volvio shouldBe true
    }

    "editar carga los datos de la receta y al guardar llama a actualizarReceta" {
        val (vm, repo) = crearViewModel()
        coEvery { repo.obtenerReceta(1) } returns brownie
        coEvery { repo.actualizarReceta(any()) } answers { firstArg() }

        vm.prepararFormulario(1)
        vm.formulario.value.titulo shouldBe "Brownie"
        vm.formulario.value.esEdicion shouldBe true

        vm.onTituloChange("Brownie con nueces")
        vm.guardarFormulario { }

        coVerify { repo.actualizarReceta(match { it.id == 1 && it.titulo == "Brownie con nueces" }) }
        vm.mensaje.value shouldBe "Receta actualizada"
    }

    "eliminar una receta existente deja el mensaje de confirmación" {
        val (vm, repo) = crearViewModel()
        coEvery { repo.eliminarReceta(1) } returns true

        vm.eliminarReceta(1)

        coVerify { repo.eliminarReceta(1) }
        vm.mensaje.value shouldBe "Receta eliminada"
    }

    "si falla la conexión al guardar, el formulario conserva lo escrito y muestra el error" {
        val (vm, repo) = crearViewModel()
        coEvery { repo.crearReceta(any()) } throws IOException("sin red")
        vm.prepararFormulario(0)
        vm.onTituloChange("Torta de mil hojas")
        vm.onCategoriaChange("Pastelería")
        vm.onTiempoPreparacionChange("120")
        vm.onDescripcionChange("Torta chilena por capas")
        vm.onIngredientesChange("Masa de hojas y manjar")
        vm.onPreparacionChange("Hornear las capas y armar con manjar")
        var volvio = false

        vm.guardarFormulario { volvio = true }

        volvio shouldBe false
        vm.formulario.value.titulo shouldBe "Torta de mil hojas"
        vm.formulario.value.guardando shouldBe false
        vm.formulario.value.mensajeError.shouldNotBeNull() shouldStartWith "No se pudo conectar"
    }
})

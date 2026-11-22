package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.model.RecetaExternaDto
import cl.duoc.mastermartini.data.model.RecetasExternasRespuesta
import cl.duoc.mastermartini.data.remote.RecetaExternaApiService
import cl.duoc.mastermartini.model.RecetaExterna
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import retrofit2.Response

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE RecetaExternaRemoteRepository (DummyJSON simulado con MockK)
// ─────────────────────────────────────────────────────────────
class RecetaExternaRemoteRepositoryTest : StringSpec({

    val dto = RecetaExternaDto(id = 1, name = "Margherita Pizza", cuisine = "Italian",
        difficulty = "Easy", prepTimeMinutes = 20, ingredients = listOf("Dough"))

    "obtenerRecetas lee la lista que viene dentro de \"recipes\"" {
        val api = mockk<RecetaExternaApiService>()
        coEvery { api.listarRecetas(any()) } returns RecetasExternasRespuesta(recipes = listOf(dto), total = 1)
        val repo = RecetaExternaRemoteRepository(api)

        runTest {
            val lista = repo.obtenerRecetas()
            lista.size shouldBe 1
            lista[0].nombre shouldBe "Margherita Pizza"
            lista[0].cocina shouldBe "Italian"
        }
    }

    "buscar con texto en blanco no llama a la búsqueda: devuelve la lista completa" {
        val api = mockk<RecetaExternaApiService>()
        coEvery { api.listarRecetas(any()) } returns RecetasExternasRespuesta(recipes = listOf(dto))
        val repo = RecetaExternaRemoteRepository(api)

        runTest {
            repo.buscarRecetas("   ").size shouldBe 1
            coVerify(exactly = 0) { api.buscarRecetas(any()) }
        }
    }

    "actualizar devuelve null si DummyJSON responde 404" {
        val api = mockk<RecetaExternaApiService>()
        coEvery { api.actualizarReceta(99, any()) } returns Response.error(404, ResponseBody.create(null, ""))
        val repo = RecetaExternaRemoteRepository(api)
        val receta = RecetaExterna(99, "Local", "Chilean", "Easy", 10, ingredientes = listOf("Pan"))

        runTest { repo.actualizarReceta(receta).shouldBeNull() }
    }
})

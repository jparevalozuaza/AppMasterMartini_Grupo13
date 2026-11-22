package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.model.RecetaDto
import cl.duoc.mastermartini.data.remote.RecetaApiService
import cl.duoc.mastermartini.model.Receta
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE RecetaRemoteRepository (Kotest + MockK, como en la Guía 15)
// La API de Retrofit se reemplaza por un mock: no se llama al servidor real.
// Se comprueba que el repositorio convierte bien los datos y maneja los códigos HTTP.
// ─────────────────────────────────────────────────────────────
class RecetaRemoteRepositoryTest : StringSpec({

    // Respuesta de error de Retrofit con el código indicado (ej.: 404 o 500)
    fun <T> error(codigo: Int): Response<T> = Response.error(codigo, ResponseBody.create(null, ""))

    val dto = RecetaDto(1, "Brownie", "Brownie húmedo", "Pastelería", 45, "Chocolate", "Hornear")

    "obtenerRecetas convierte el JSON del servidor (RecetaDto) en Receta" {
        val api = mockk<RecetaApiService>()
        coEvery { api.listarRecetas() } returns listOf(dto)
        val repo = RecetaRemoteRepository(api)

        runTest {
            val resultado = repo.obtenerRecetas()
            resultado.size shouldBe 1
            resultado[0].titulo shouldBe "Brownie"
            resultado[0].tiempoPreparacion shouldBe 45
        }
    }

    "obtenerReceta devuelve null cuando el servidor responde 404" {
        val api = mockk<RecetaApiService>()
        coEvery { api.obtenerReceta(99) } returns error(404)
        val repo = RecetaRemoteRepository(api)

        runTest { repo.obtenerReceta(99).shouldBeNull() }
    }

    "crearReceta envía la receta SIN id para que el servidor lo asigne" {
        val api = mockk<RecetaApiService>()
        coEvery { api.crearReceta(any()) } answers { firstArg<RecetaDto>().copy(id = 7) }
        val repo = RecetaRemoteRepository(api)
        val nueva = Receta(0, "Kuchen", "Kuchen de nuez", "Pastelería", 60, "Nueces", "Hornear")

        runTest {
            repo.crearReceta(nueva).id shouldBe 7
            coVerify { api.crearReceta(match { it.id == null && it.titulo == "Kuchen" }) }
        }
    }

    "eliminarReceta devuelve true con 204 y false con 404" {
        val api = mockk<RecetaApiService>()
        coEvery { api.eliminarReceta(1) } returns Response.success(204, Unit)
        coEvery { api.eliminarReceta(99) } returns error(404)
        val repo = RecetaRemoteRepository(api)

        runTest {
            repo.eliminarReceta(1) shouldBe true
            repo.eliminarReceta(99) shouldBe false
        }
    }

    "un error 500 del servidor se transforma en HttpException" {
        val api = mockk<RecetaApiService>()
        coEvery { api.actualizarReceta(1, any()) } returns error(500)
        val repo = RecetaRemoteRepository(api)
        val receta = Receta(1, "Brownie", "Brownie húmedo", "Pastelería", 45, "Chocolate", "Hornear")

        runTest {
            val excepcion = shouldThrow<HttpException> { repo.actualizarReceta(receta) }
            excepcion.code() shouldBe 500
        }
    }
})

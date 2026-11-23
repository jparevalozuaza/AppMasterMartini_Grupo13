package cl.duoc.mastermartini.data.remote

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE LA CONFIGURACIÓN DE RETROFIT
// Crear los servicios NO hace ninguna llamada de red: Retrofit solo
// prepara el objeto. La conexión real se prueba con el microservicio encendido.
// ─────────────────────────────────────────────────────────────
class RetrofitInstanceTest : StringSpec({

    "crea los servicios del microservicio y de DummyJSON una sola vez (by lazy)" {
        val api = RetrofitInstance.recetaApi
        api.shouldBeInstanceOf<RecetaApiService>()
        RetrofitInstance.recetaApi shouldBeSameInstanceAs api

        RetrofitInstance.recetaExternaApi.shouldBeInstanceOf<RecetaExternaApiService>()
    }

    "las direcciones base usan http(s) y terminan en / como exige Retrofit" {
        ApiConfig.BACKEND_BASE_URL shouldStartWith "http"
        ApiConfig.BACKEND_BASE_URL shouldEndWith "/"
        ApiConfig.EXTERNA_BASE_URL shouldBe "https://dummyjson.com/"
        (ApiConfig.TIEMPO_ESPERA_SEGUNDOS > 0) shouldBe true
    }
})

package cl.duoc.mastermartini.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// ─────────────────────────────────────────────────────────────
// INSTANCIA DE RETROFIT (misma estructura que la Guía 14)
// "object"  = existe una sola instancia en toda la app.
// "by lazy" = se crea recién la primera vez que se usa, y una sola vez.
// ─────────────────────────────────────────────────────────────
object RetrofitInstance {

    // Cliente HTTP con tiempo máximo de espera: si el servidor no responde
    // en ese tiempo, la llamada falla y la app muestra un mensaje de error
    // en vez de quedarse cargando para siempre.
    // (OkHttp viene incluido con Retrofit; no es una dependencia nueva.)
    private val clienteHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(ApiConfig.TIEMPO_ESPERA_SEGUNDOS, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.TIEMPO_ESPERA_SEGUNDOS, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.TIEMPO_ESPERA_SEGUNDOS, TimeUnit.SECONDS)
            .build()
    }

    // Servicio del microservicio de recetas (backend del Integrante 1)
    val recetaApi: RecetaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BACKEND_BASE_URL)                  // http://10.0.2.2:8080/
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())   // JSON ⇄ objetos Kotlin
            .build()
            .create(RecetaApiService::class.java)                 // Retrofit implementa la interfaz
    }

    // Servicio de la API externa DummyJSON (otra URL base, mismo cliente HTTP).
    // Cada servidor necesita su propio Retrofit porque la URL base es distinta.
    val recetaExternaApi: RecetaExternaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.EXTERNA_BASE_URL)                  // https://dummyjson.com/
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecetaExternaApiService::class.java)
    }
}

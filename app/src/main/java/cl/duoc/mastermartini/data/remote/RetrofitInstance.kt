package cl.duoc.mastermartini.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ─────────────────────────────────────────────────────────────
// GUÍA 14 · INSTANCIA DE RETROFIT
// "object" = singleton: existe una sola instancia en toda la app.
// "by lazy" = el servicio se crea recién la primera vez que se usa, y una sola vez.
// ─────────────────────────────────────────────────────────────
object RetrofitInstance {

    // URL base de la API pública de práctica (HTTPS). Debe terminar en "/".
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)                                   // servidor al que se conecta
            .addConverterFactory(GsonConverterFactory.create())  // JSON ⇄ objetos Kotlin
            .build()
            .create(ApiService::class.java)                      // Retrofit implementa la interfaz
    }
}

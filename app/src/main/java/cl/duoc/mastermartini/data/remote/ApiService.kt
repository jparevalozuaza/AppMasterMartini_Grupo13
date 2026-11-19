package cl.duoc.mastermartini.data.remote

import cl.duoc.mastermartini.model.Post
import retrofit2.http.GET

// ─────────────────────────────────────────────────────────────
// GUÍA 14 · ENDPOINTS DE LA API
// Cada función describe una petición HTTP; Retrofit escribe el código que la ejecuta.
// "suspend" = la función se ejecuta en segundo plano (corrutina) sin bloquear la pantalla.
// ─────────────────────────────────────────────────────────────
interface ApiService {

    // GET /posts → obtiene la lista completa de posts
    @GET("posts")
    suspend fun getPosts(): List<Post>

}

package cl.duoc.mastermartini.data.remote

import cl.duoc.mastermartini.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// ─────────────────────────────────────────────────────────────
// GUÍA 14 · ENDPOINTS DE LA API
// Cada función describe una petición HTTP; Retrofit escribe el código que la ejecuta.
// "suspend" = la función se ejecuta en segundo plano (corrutina) sin bloquear la pantalla.
// ─────────────────────────────────────────────────────────────
interface ApiService {

    // GET /posts → obtiene la lista completa de posts
    @GET("posts")
    suspend fun getPosts(): List<Post>

    // POST /posts → crea un post; el post viaja como JSON en el cuerpo (@Body)
    @POST("posts")
    suspend fun createPost(@Body post: Post): Post

    // PUT /posts/{id} → reemplaza el post indicado; {id} se completa con @Path
    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Post

    // DELETE /posts/{id} → elimina el post; la respuesta no trae datos,
    // por eso se pide la Response para revisar solo si fue exitosa (código 2xx)
    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<Unit>
}

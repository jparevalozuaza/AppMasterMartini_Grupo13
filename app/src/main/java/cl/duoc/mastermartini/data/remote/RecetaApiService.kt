package cl.duoc.mastermartini.data.remote

import cl.duoc.mastermartini.data.model.RecetaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// ─────────────────────────────────────────────────────────────
// ENDPOINTS DEL MICROSERVICIO DE RECETAS (misma estructura que la Guía 14)
// Cada función describe UNA petición HTTP; Retrofit escribe el código que la ejecuta.
// Las rutas NO empiezan con "/" porque se suman a la URL base, que ya termina en "/":
//   http://10.0.2.2:8080/  +  api/recetas  =  http://10.0.2.2:8080/api/recetas
// "suspend" = se ejecuta dentro de una corrutina, sin congelar la pantalla.
// ─────────────────────────────────────────────────────────────
interface RecetaApiService {

    // GET /api/recetas → lista completa (código 200)
    @GET("api/recetas")
    suspend fun listarRecetas(): List<RecetaDto>

    // GET /api/recetas/{id} → una receta (200) o 404 si no existe.
    // Se pide la Response completa para poder distinguir el 404 sin que sea un error.
    @GET("api/recetas/{id}")
    suspend fun obtenerReceta(@Path("id") id: Int): Response<RecetaDto>

    // POST /api/recetas → crea una receta; responde 201 con la receta y su id nuevo
    @POST("api/recetas")
    suspend fun crearReceta(@Body receta: RecetaDto): RecetaDto

    // PUT /api/recetas/{id} → reemplaza la receta (200) o 404 si no existe
    @PUT("api/recetas/{id}")
    suspend fun actualizarReceta(@Path("id") id: Int, @Body receta: RecetaDto): Response<RecetaDto>

    // DELETE /api/recetas/{id} → 204 (sin contenido) o 404.
    // Response<Unit> = no esperamos datos, solo revisar si salió bien.
    @DELETE("api/recetas/{id}")
    suspend fun eliminarReceta(@Path("id") id: Int): Response<Unit>
}

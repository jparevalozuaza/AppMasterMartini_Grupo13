package cl.duoc.mastermartini.data.remote

import cl.duoc.mastermartini.data.model.RecetaExternaDto
import cl.duoc.mastermartini.data.model.RecetasExternasRespuesta
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// ─────────────────────────────────────────────────────────────
// ENDPOINTS DE LA API EXTERNA DummyJSON (ciclo CRUD completo)
// Base: https://dummyjson.com/  (ApiConfig.EXTERNA_BASE_URL)
// POST, PUT y DELETE son SIMULADOS por DummyJSON: responden con éxito
// y devuelven la receta, pero no la guardan en su servidor.
// ─────────────────────────────────────────────────────────────
interface RecetaExternaApiService {

    // GET /recipes?limit=30 → { "recipes": [...], "total": ... }
    // @Query agrega "?limit=30" al final de la ruta
    @GET("recipes")
    suspend fun listarRecetas(@Query("limit") limite: Int = 30): RecetasExternasRespuesta

    // GET /recipes/search?q=pasta → mismas recetas, filtradas por texto
    @GET("recipes/search")
    suspend fun buscarRecetas(@Query("q") texto: String): RecetasExternasRespuesta

    // POST /recipes/add → devuelve la receta enviada con un id nuevo
    @POST("recipes/add")
    suspend fun crearReceta(@Body receta: RecetaExternaDto): RecetaExternaDto

    // PUT /recipes/{id} → devuelve la receta actualizada (404 si el id no existe en DummyJSON)
    @PUT("recipes/{id}")
    suspend fun actualizarReceta(
        @Path("id") id: Int,
        @Body receta: RecetaExternaDto
    ): Response<RecetaExternaDto>

    // DELETE /recipes/{id} → devuelve la receta con "isDeleted": true (404 si no existe)
    @DELETE("recipes/{id}")
    suspend fun eliminarReceta(@Path("id") id: Int): Response<RecetaExternaDto>
}

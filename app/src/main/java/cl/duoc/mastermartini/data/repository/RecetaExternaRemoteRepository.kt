package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.model.aDto
import cl.duoc.mastermartini.data.model.aRecetaExterna
import cl.duoc.mastermartini.data.remote.RecetaExternaApiService
import cl.duoc.mastermartini.data.remote.RetrofitInstance
import cl.duoc.mastermartini.model.RecetaExterna
import retrofit2.HttpException

// ─────────────────────────────────────────────────────────────
// REPOSITORIO REMOTO DE LA API EXTERNA: cumple el contrato llamando a DummyJSON.
// El servicio llega como parámetro (con valor por defecto) para poder
// reemplazarlo por un "mock" en las pruebas unitarias (Bloque 5).
// ─────────────────────────────────────────────────────────────
class RecetaExternaRemoteRepository(
    private val api: RecetaExternaApiService = RetrofitInstance.recetaExternaApi
) : RecetaExternaRepository {

    override suspend fun obtenerRecetas(): List<RecetaExterna> =
        api.listarRecetas().recipes.orEmpty().map { it.aRecetaExterna() }

    override suspend fun buscarRecetas(texto: String): List<RecetaExterna> {
        val limpio = texto.trim()
        if (limpio.isEmpty()) return obtenerRecetas()
        return api.buscarRecetas(limpio).recipes.orEmpty().map { it.aRecetaExterna() }
    }

    override suspend fun crearReceta(receta: RecetaExterna): RecetaExterna =
        // La respuesta trae el id nuevo; los demás datos se completan con los enviados
        api.crearReceta(receta.aDto()).aRecetaExterna(respaldo = receta)

    override suspend fun actualizarReceta(receta: RecetaExterna): RecetaExterna? {
        val respuesta = api.actualizarReceta(receta.id, receta.aDto())
        return when {
            respuesta.isSuccessful -> respuesta.body()?.aRecetaExterna(respaldo = receta) ?: receta
            respuesta.code() == 404 -> null                  // DummyJSON no conoce ese id
            else -> throw HttpException(respuesta)
        }
    }

    override suspend fun eliminarReceta(id: Int): Boolean {
        val respuesta = api.eliminarReceta(id)
        return when {
            respuesta.isSuccessful -> true
            respuesta.code() == 404 -> false
            else -> throw HttpException(respuesta)
        }
    }
}

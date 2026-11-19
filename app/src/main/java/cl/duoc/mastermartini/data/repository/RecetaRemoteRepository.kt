package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.model.aDto
import cl.duoc.mastermartini.data.model.aReceta
import cl.duoc.mastermartini.data.remote.RecetaApiService
import cl.duoc.mastermartini.data.remote.RetrofitInstance
import cl.duoc.mastermartini.model.Receta
import retrofit2.HttpException

// ─────────────────────────────────────────────────────────────
// REPOSITORIO REMOTO: cumple el contrato usando el microservicio real.
// Convierte entre RecetaDto (red) y Receta (app) en cada operación.
// El servicio se recibe como parámetro (con valor por defecto) para poder
// reemplazarlo por un "mock" en las pruebas unitarias (Bloque 5).
// ─────────────────────────────────────────────────────────────
class RecetaRemoteRepository(
    private val api: RecetaApiService = RetrofitInstance.recetaApi
) : RecetaRepository {

    override suspend fun obtenerRecetas(): List<Receta> =
        api.listarRecetas().map { it.aReceta() }

    override suspend fun obtenerReceta(id: Int): Receta? {
        val respuesta = api.obtenerReceta(id)
        return when {
            respuesta.isSuccessful -> respuesta.body()?.aReceta()
            respuesta.code() == 404 -> null                // no existe: no es un error
            else -> throw HttpException(respuesta)        // otro problema del servidor
        }
    }

    override suspend fun crearReceta(receta: Receta): Receta =
        api.crearReceta(receta.aDto()).aReceta()

    override suspend fun actualizarReceta(receta: Receta): Receta? {
        val respuesta = api.actualizarReceta(receta.id, receta.aDto())
        return when {
            respuesta.isSuccessful -> respuesta.body()?.aReceta()
            respuesta.code() == 404 -> null
            else -> throw HttpException(respuesta)
        }
    }

    override suspend fun eliminarReceta(id: Int): Boolean {
        val respuesta = api.eliminarReceta(id)
        return when {
            respuesta.isSuccessful -> true                 // 204: eliminada
            respuesta.code() == 404 -> false               // ya no existía
            else -> throw HttpException(respuesta)
        }
    }
}

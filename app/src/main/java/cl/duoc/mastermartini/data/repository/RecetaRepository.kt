package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.dao.RecetaDao
import cl.duoc.mastermartini.data.entity.RecetaEntity
import kotlinx.coroutines.flow.Flow

class RecetaRepository(
    private val recetaDao: RecetaDao
) {

    fun obtenerTodas(): Flow<List<RecetaEntity>> {
        return recetaDao.obtenerTodas()
    }

    suspend fun obtenerPorId(id: Int): RecetaEntity? {
        return recetaDao.obtenerPorId(id)
    }

    suspend fun contarPorTitulo(titulo: String): Int =
        recetaDao.contarPorTitulo(titulo)

    suspend fun insertar(receta: RecetaEntity) {
        recetaDao.insertar(receta)
    }

    suspend fun eliminar(receta: RecetaEntity) {
        recetaDao.eliminar(receta)
    }
}

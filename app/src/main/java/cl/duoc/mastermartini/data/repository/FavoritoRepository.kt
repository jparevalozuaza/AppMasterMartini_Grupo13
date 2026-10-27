package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.dao.FavoritoDao
import cl.duoc.mastermartini.data.entity.FavoritoEntity
import kotlinx.coroutines.flow.Flow


class FavoritoRepository(
    private val favoritoDao: FavoritoDao
) {

    fun obtenerTodos(): Flow<List<FavoritoEntity>> {
        return favoritoDao.obtenerTodos()
    }

    suspend fun insertar(contenidoId: Int) {
        favoritoDao.insertar(FavoritoEntity(contenidoId))
    }

    suspend fun eliminar(contenidoId: Int) {
        favoritoDao.eliminar(FavoritoEntity(contenidoId))
    }
}

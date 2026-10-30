package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.dao.ProgresoDao
import cl.duoc.mastermartini.data.entity.ProgresoEntity
import kotlinx.coroutines.flow.Flow

class ProgresoRepository(
    private val progresoDao: ProgresoDao
) {

    fun obtenerTodos(): Flow<List<ProgresoEntity>> {
        return progresoDao.obtenerTodos()
    }

    suspend fun marcarCompletado(contenidoId: Int) {
        progresoDao.guardar(
            ProgresoEntity(
                contenidoId = contenidoId,
                completado = true
            )
        )
    }

    suspend fun marcarPendiente(contenidoId: Int) {
        progresoDao.eliminar(contenidoId)
    }
}

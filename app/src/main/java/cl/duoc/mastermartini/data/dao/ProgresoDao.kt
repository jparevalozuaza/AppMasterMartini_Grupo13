package cl.duoc.mastermartini.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.mastermartini.data.entity.ProgresoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgresoDao {

    @Query("SELECT * FROM progreso")
    fun obtenerTodos(): Flow<List<ProgresoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(progreso: ProgresoEntity)

    @Query("DELETE FROM progreso WHERE contenidoId = :contenidoId")
    suspend fun eliminar(contenidoId: Int)
}

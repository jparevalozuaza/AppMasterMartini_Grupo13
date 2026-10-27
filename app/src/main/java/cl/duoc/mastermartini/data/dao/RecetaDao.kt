package cl.duoc.mastermartini.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import cl.duoc.mastermartini.data.entity.RecetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {

    @Query("SELECT * FROM recetas ORDER BY titulo ASC")
    fun obtenerTodas(): Flow<List<RecetaEntity>>

    @Query("SELECT * FROM recetas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): RecetaEntity?

    @Query("SELECT COUNT(*) FROM recetas WHERE titulo = :titulo")
    suspend fun contarPorTitulo(titulo: String): Int

    @Insert
    suspend fun insertar(receta: RecetaEntity)

    @Insert
    suspend fun insertarTodas(recetas: List<RecetaEntity>)

    @Delete
    suspend fun eliminar(receta: RecetaEntity)

}

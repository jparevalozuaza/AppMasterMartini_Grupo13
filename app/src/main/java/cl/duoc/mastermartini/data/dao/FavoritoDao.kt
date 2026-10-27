package cl.duoc.mastermartini.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import cl.duoc.mastermartini.data.entity.FavoritoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {

    @Query("SELECT * FROM favoritos")
    fun obtenerTodos(): Flow<List<FavoritoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(favorito: FavoritoEntity)

    @Delete
    suspend fun eliminar(favorito: FavoritoEntity)
}

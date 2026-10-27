package cl.duoc.mastermartini.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey
    val contenidoId: Int
)

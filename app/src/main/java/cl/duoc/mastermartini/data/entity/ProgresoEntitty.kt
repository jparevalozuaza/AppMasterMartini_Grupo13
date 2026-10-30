package cl.duoc.mastermartini.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progreso")
data class ProgresoEntity(
    @PrimaryKey
    val contenidoId: Int,
    val completado: Boolean = false
)

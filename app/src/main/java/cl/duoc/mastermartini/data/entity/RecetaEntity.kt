package cl.duoc.mastermartini.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class RecetaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val tiempoPreparacion: Int,
    val ingredientes: String,
    val preparacion: String
)

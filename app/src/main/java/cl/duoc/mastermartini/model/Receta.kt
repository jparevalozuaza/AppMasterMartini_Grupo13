package cl.duoc.mastermartini.model

data class Receta(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val tiempoPreparacion: Int,
    val ingredientes: String,
    val preparacion: String
)

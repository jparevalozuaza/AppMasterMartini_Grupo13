package cl.duoc.mastermartini.model

data class Curso(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val lecciones: List<Leccion>
)

package cl.duoc.mastermartini.data.model

import cl.duoc.mastermartini.model.Receta

// ─────────────────────────────────────────────────────────────
// MODELO DE COMUNICACIÓN (DTO = "objeto de transferencia de datos")
// Representa una receta EXACTAMENTE como viaja por la red, en el JSON
// del microservicio de recetas (contrato acordado con el Integrante 1):
//   { "id": 1, "titulo": "...", "descripcion": "...", "categoria": "...",
//     "tiempoPreparacion": 45, "ingredientes": "...", "preparacion": "..." }
// Gson usa los NOMBRES de estas propiedades para leer y escribir el JSON,
// por eso deben ser idénticos a los del backend.
//
// Todas las propiedades admiten null (?): si el servidor omite un campo,
// la app no se cae; la conversión de abajo pone un valor por defecto.
// ─────────────────────────────────────────────────────────────
data class RecetaDto(
    val id: Int? = null,                // null al CREAR: el id lo asigna MySQL
    val titulo: String? = null,
    val descripcion: String? = null,
    val categoria: String? = null,
    val tiempoPreparacion: Int? = null, // en minutos
    val ingredientes: String? = null,
    val preparacion: String? = null
)

// ─────────────────────────────────────────────────────────────
// CONVERSIONES entre el modelo de la red (RecetaDto) y el modelo
// que usa la app en sus pantallas (model/Receta).
// Así, si el backend cambia un detalle, solo se ajusta este archivo.
// ─────────────────────────────────────────────────────────────

// Del servidor a la app: los campos vacíos se reemplazan por valores seguros
fun RecetaDto.aReceta(): Receta = Receta(
    id = id ?: 0,
    titulo = titulo.orEmpty(),
    descripcion = descripcion.orEmpty(),
    categoria = categoria.orEmpty(),
    tiempoPreparacion = tiempoPreparacion ?: 0,
    ingredientes = ingredientes.orEmpty(),
    preparacion = preparacion.orEmpty()
)

// De la app al servidor: id 0 significa "receta nueva", y se envía sin id.
// (Gson omite las propiedades null, así el backend sabe que debe crearla.)
fun Receta.aDto(): RecetaDto = RecetaDto(
    id = if (id == 0) null else id,
    titulo = titulo,
    descripcion = descripcion,
    categoria = categoria,
    tiempoPreparacion = tiempoPreparacion,
    ingredientes = ingredientes,
    preparacion = preparacion
)

package cl.duoc.mastermartini.ui.state

import cl.duoc.mastermartini.model.Receta

// ─────────────────────────────────────────────────────────────
// ESTADO DEL FORMULARIO DE RECETAS (crear y editar)
// Un formulario necesita dos cosas por cada campo:
//   1. lo que el usuario escribió (siempre texto, incluso el tiempo)
//   2. su mensaje de error (null = sin error)
// Además guarda si está cargando la receta a editar, si está guardando
// y un error general (por ejemplo, sin conexión al guardar).
// ─────────────────────────────────────────────────────────────
data class RecetaFormUiState(
    val id: Int = 0,                         // 0 = receta nueva; otro valor = edición
    // Campos
    val titulo: String = "",
    val categoria: String = "",
    val tiempoPreparacion: String = "",      // texto: el usuario puede escribir cualquier cosa
    val descripcion: String = "",
    val ingredientes: String = "",
    val preparacion: String = "",
    // Errores de validación (uno por campo)
    val tituloError: String? = null,
    val categoriaError: String? = null,
    val tiempoPreparacionError: String? = null,
    val descripcionError: String? = null,
    val ingredientesError: String? = null,
    val preparacionError: String? = null,
    // Situación del formulario
    val cargando: Boolean = false,           // trayendo la receta que se va a editar
    val guardando: Boolean = false,          // esperando la respuesta del servidor
    val mensajeError: String? = null         // error general (no de un campo)
) {
    // true si se está editando una receta que ya existe
    val esEdicion: Boolean get() = id != 0
}

// Del formulario a una Receta lista para enviar (textos sin espacios sobrantes)
fun RecetaFormUiState.aReceta(): Receta = Receta(
    id = id,
    titulo = titulo.trim(),
    descripcion = descripcion.trim(),
    categoria = categoria.trim(),
    tiempoPreparacion = tiempoPreparacion.trim().toIntOrNull() ?: 0,
    ingredientes = ingredientes.trim(),
    preparacion = preparacion.trim()
)

// De una Receta existente al formulario (para editarla)
fun Receta.aFormulario(): RecetaFormUiState = RecetaFormUiState(
    id = id,
    titulo = titulo,
    categoria = categoria,
    tiempoPreparacion = tiempoPreparacion.toString(),
    descripcion = descripcion,
    ingredientes = ingredientes,
    preparacion = preparacion
)

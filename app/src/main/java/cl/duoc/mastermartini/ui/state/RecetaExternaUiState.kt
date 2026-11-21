package cl.duoc.mastermartini.ui.state

import cl.duoc.mastermartini.model.RecetaExterna

// ─────────────────────────────────────────────────────────────
// ESTADOS DE LA PANTALLA "RECETAS DEL MUNDO" (API externa)
// ─────────────────────────────────────────────────────────────
sealed interface RecetaExternaUiState {
    data object Cargando : RecetaExternaUiState
    data class Exito(val recetas: List<RecetaExterna>) : RecetaExternaUiState   // puede venir vacía
    data class Error(val mensaje: String) : RecetaExternaUiState
}

// ─────────────────────────────────────────────────────────────
// FORMULARIO DE LA API EXTERNA (crear y editar), mismo esquema que RecetaFormUiState:
// campos (como texto) + un error por campo + situación del formulario.
// ─────────────────────────────────────────────────────────────
data class RecetaExternaFormUiState(
    val visible: Boolean = false,            // el formulario se muestra en un diálogo
    val id: Int = 0,                         // 0 = nueva (POST); otro valor = edición (PUT)
    val nombre: String = "",
    val cocina: String = "",
    val minutos: String = "",
    val ingredientes: String = "",           // separados por coma
    val nombreError: String? = null,
    val cocinaError: String? = null,
    val minutosError: String? = null,
    val ingredientesError: String? = null,
    val guardando: Boolean = false
) {
    val esEdicion: Boolean get() = id != 0
}

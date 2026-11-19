package cl.duoc.mastermartini.ui.state

import cl.duoc.mastermartini.model.Post

// ─────────────────────────────────────────────────────────────
// GUÍA 14 · ESTADOS DE LA PANTALLA DE POSTS
// Una llamada a Internet puede estar en uno de tres estados.
// "sealed interface" = lista cerrada: la pantalla está obligada a considerar los tres.
// ─────────────────────────────────────────────────────────────
sealed interface PostUiState {
    data object Cargando : PostUiState                           // esperando la respuesta
    data class Exito(val posts: List<Post>) : PostUiState        // llegaron los datos
    data class Error(val mensaje: String) : PostUiState          // falló la conexión
}

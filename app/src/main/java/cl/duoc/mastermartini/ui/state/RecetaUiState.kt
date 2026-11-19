package cl.duoc.mastermartini.ui.state

import cl.duoc.mastermartini.model.Receta

// ─────────────────────────────────────────────────────────────
// ESTADOS DE LA PANTALLA DE RECETAS
// Pedir datos a un servidor puede estar en una de tres situaciones.
// "sealed interface" = lista CERRADA: la pantalla está obligada a
// considerar las tres (el compilador avisa si falta una en un "when").
// ─────────────────────────────────────────────────────────────
sealed interface RecetaUiState {

    // Esperando la respuesta: la pantalla muestra un indicador de carga
    data object Cargando : RecetaUiState

    // Llegaron los datos (la lista puede venir vacía si no hay recetas)
    data class Exito(val recetas: List<Receta>) : RecetaUiState

    // Algo falló (sin conexión, servidor apagado, error del servidor)
    data class Error(val mensaje: String) : RecetaUiState
}

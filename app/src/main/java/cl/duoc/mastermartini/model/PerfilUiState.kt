package cl.duoc.mastermartini.model

import cl.duoc.mastermartini.data.entity.UsuarioEntity

// Estado de la pantalla Perfil
data class PerfilUiState(
    val cargando: Boolean = true,
    val usuario: UsuarioEntity? = null,
    val editando: Boolean = false,      // true = se muestran los campos de edición
    // Campos del formulario de edición
    val nombre: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val nombreError: String? = null,
    val telefonoError: String? = null,
    val guardando: Boolean = false,
    val guardandoFoto: Boolean = false, // true mientras se copia la imagen elegida en la galería
    val mensaje: String? = null         // mensaje breve para mostrar en Snackbar
)

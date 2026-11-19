package cl.duoc.mastermartini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.repository.PostRepository
import cl.duoc.mastermartini.model.Post
import cl.duoc.mastermartini.ui.state.PostUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
// GUÍA 14 · VIEWMODEL DE POSTS
// Mantiene el estado de la pantalla y ejecuta las operaciones CRUD.
// Nota sobre JSONPlaceholder: es una API de práctica. Responde como si guardara
// los cambios, pero NO los guarda. Por eso la lista se actualiza en memoria
// con lo que responde el servidor.
// ─────────────────────────────────────────────────────────────
class PostViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    // Estado principal: Cargando, Exito (con la lista) o Error
    private val _estado = MutableStateFlow<PostUiState>(PostUiState.Cargando)
    val estado: StateFlow<PostUiState> = _estado.asStateFlow()

    // Mensaje breve para mostrar en un Snackbar (null = sin mensaje)
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // Se llama automáticamente al crear el ViewModel
    init {
        cargarPosts()
    }

    // ===================== GET =====================
    fun cargarPosts() {
        viewModelScope.launch {
            _estado.value = PostUiState.Cargando
            _estado.value = try {
                PostUiState.Exito(repository.getPosts())
            } catch (e: Exception) {
                PostUiState.Error("No se pudieron cargar los posts. Revisa tu conexión a Internet.")
            }
        }
    }

    // ===================== POST =====================
    fun crearPost(titulo: String, cuerpo: String) {
        if (titulo.isBlank() || cuerpo.isBlank()) {
            _mensaje.value = "Completa el título y el contenido"
            return
        }
        viewModelScope.launch {
            try {
                val creado = repository.createPost(
                    Post(userId = 1, id = 0, title = titulo.trim(), body = cuerpo.trim())
                )
                val lista = listaActual()
                // JSONPlaceholder siempre responde id = 101: se asegura un id único en la lista
                val idUnico = maxOf(creado.id, (lista.maxOfOrNull { it.id } ?: 0) + 1)
                _estado.value = PostUiState.Exito(listOf(creado.copy(id = idUnico)) + lista)
                _mensaje.value = "Post creado (POST · respuesta ${creado.id})"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo crear el post"
            }
        }
    }

    // ===================== PUT =====================
    fun actualizarPost(post: Post, titulo: String, cuerpo: String) {
        if (titulo.isBlank() || cuerpo.isBlank()) {
            _mensaje.value = "Completa el título y el contenido"
            return
        }
        val editado = post.copy(title = titulo.trim(), body = cuerpo.trim())
        viewModelScope.launch {
            try {
                // Los posts creados en esta sesión (id > 100) no existen en el servidor
                // de práctica: un PUT sobre ellos falla, así que se actualizan solo en memoria.
                val resultado = if (post.id <= 100) repository.updatePost(editado) else editado
                _estado.value = PostUiState.Exito(
                    listaActual().map { if (it.id == post.id) resultado else it }
                )
                _mensaje.value = "Post actualizado (PUT)"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo actualizar el post"
            }
        }
    }

    // ===================== DELETE =====================
    fun eliminarPost(post: Post) {
        viewModelScope.launch {
            try {
                if (repository.deletePost(post.id)) {
                    _estado.value = PostUiState.Exito(listaActual().filterNot { it.id == post.id })
                    _mensaje.value = "Post eliminado (DELETE)"
                } else {
                    _mensaje.value = "El servidor rechazó la eliminación"
                }
            } catch (e: Exception) {
                _mensaje.value = "No se pudo eliminar el post"
            }
        }
    }

    fun consumirMensaje() {
        _mensaje.value = null
    }

    // Lista que se está mostrando (vacía si todavía no hay datos)
    private fun listaActual(): List<Post> =
        (_estado.value as? PostUiState.Exito)?.posts ?: emptyList()
}

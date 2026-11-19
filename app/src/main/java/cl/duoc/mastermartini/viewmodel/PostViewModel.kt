package cl.duoc.mastermartini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.repository.PostRepository
import cl.duoc.mastermartini.ui.state.PostUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
// GUÍA 14 · VIEWMODEL DE POSTS
// Mantiene el estado de la pantalla y pide los datos al repositorio.
// ─────────────────────────────────────────────────────────────
class PostViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    // Estado principal: Cargando, Exito (con la lista) o Error.
    // "_estado" es privado y modificable; "estado" es público y de solo lectura.
    private val _estado = MutableStateFlow<PostUiState>(PostUiState.Cargando)
    val estado: StateFlow<PostUiState> = _estado.asStateFlow()

    // Se llama automáticamente al crear el ViewModel
    init {
        cargarPosts()
    }

    // ===================== GET =====================
    fun cargarPosts() {
        viewModelScope.launch {                       // corrutina atada a la vida del ViewModel
            _estado.value = PostUiState.Cargando
            _estado.value = try {
                PostUiState.Exito(repository.getPosts())
            } catch (e: Exception) {                  // sin Internet, servidor caído, JSON inválido…
                PostUiState.Error("No se pudieron cargar los posts. Revisa tu conexión a Internet.")
            }
        }
    }
}


package cl.duoc.mastermartini.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.database.AppDatabase
import cl.duoc.mastermartini.data.repository.FavoritoRepository
import cl.duoc.mastermartini.navigation.NavigationEvent
import cl.duoc.mastermartini.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// ViewModel: administra el estado y las acciones de las pantallas.
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Repositorio que permite guardar y consultar favoritos con Room.
    private val favoritoDao =
        AppDatabase.getDatabase(application).favoritoDao()

    private val favoritoRepository =
        FavoritoRepository(favoritoDao)

    // Canal de eventos para la navegación.
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    val navigationEvents: SharedFlow<NavigationEvent> =
        _navigationEvents.asSharedFlow()

    // Estado de los identificadores de contenidos favoritos.
    private val _favoritos = mutableStateOf(setOf<Int>())

    val favoritos: State<Set<Int>> = _favoritos

    // Carga los favoritos guardados y observa sus cambios.
    init {
        viewModelScope.launch {
            favoritoRepository.obtenerTodos().collect { lista ->
                _favoritos.value = lista.map { it.contenidoId }.toSet()
            }
        }
    }

    // Comprueba si un contenido está marcado como favorito.
    fun esFavorito(contenidoId: Int): Boolean {
        return _favoritos.value.contains(contenidoId)
    }

    // Guarda o elimina un favorito en Room.
    fun alternarFavorito(contenidoId: Int) {
        viewModelScope.launch {
            val esFavoritoActual =
                _favoritos.value.contains(contenidoId)

            if (esFavoritoActual) {
                favoritoRepository.eliminar(contenidoId)
            } else {
                favoritoRepository.insertar(contenidoId)
            }
        }
    }

    // Navega a una pantalla y mantiene Inicio como base del historial.
    fun navigateTo(screen: Screen) {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(
                    route = screen,
                    popUpToRoute = Screen.Home,
                    singleTop = true
                )
            )
        }
    }

    // Vuelve a la pantalla anterior.
    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.PopBackStack
            )
        }
    }

    // Sube un nivel en la jerarquía de navegación.
    fun navigateUp() {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateUp
            )
        }
    }

    // Navega a una pantalla eliminando del historial la pantalla indicada.
    // Se utiliza al iniciar y cerrar sesión.
    fun navegarLimpiando(destino: Screen, limpiarHasta: Screen) {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(
                    route = destino,
                    popUpToRoute = limpiarHasta,
                    inclusive = true,
                    singleTop = true
                )
            )
        }
    }

    // Navega directamente a una ruta.
    fun navigateToRoute(route: String) {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateToRoute(route)
            )
        }
    }
}

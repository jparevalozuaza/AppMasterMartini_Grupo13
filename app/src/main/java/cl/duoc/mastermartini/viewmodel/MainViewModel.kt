package cl.duoc.mastermartini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.navigation.NavigationEvent
import cl.duoc.mastermartini.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// ViewModel = la "ventanilla" que recibe pedidos desde las pantallas.
// Sobrevive a cambios como girar el celular (las pantallas, en cambio, se redibujan).
class MainViewModel : ViewModel() {

    // FLOW = un "canal" por donde viajan los eventos.
    // El guion bajo (_) indica que es privado: solo el ViewModel puede ENVIAR por él.
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    // Versión de solo lectura: MainActivity puede ESCUCHAR, pero no enviar.
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // Ir a una pantalla.
    // popUpToRoute = Home: Inicio siempre queda como "base" del historial,
    //   así el botón "atrás" desde cualquier pantalla devuelve a Inicio.
    // singleTop = true: si ya estás en esa pantalla, no se abre otra copia encima.
    fun navigateTo(screen: Screen) {
        // viewModelScope.launch = ejecuta la tarea en segundo plano,
        // y la cancela automáticamente si el ViewModel deja de existir.
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

    // Volver a la pantalla anterior
    fun navigateBack() {
        viewModelScope.launch { _navigationEvents.emit(NavigationEvent.PopBackStack) }
    }

    // Subir un nivel en la jerarquía
    fun navigateUp() {
        viewModelScope.launch { _navigationEvents.emit(NavigationEvent.NavigateUp) }
    }
}
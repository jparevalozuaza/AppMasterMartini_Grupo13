package cl.duoc.mastermartini.navigation

// Todos los movimientos de navegación posibles en la app
sealed class NavigationEvent {

    // Ir a una pantalla específica
    data class NavigateTo(
        val route: Screen,                 // destino (un Screen, no un texto suelto)
        val popUpToRoute: Screen? = null,  // si no es null, "limpia" el historial hasta esta pantalla
        val inclusive: Boolean = false,    // true = también elimina popUpToRoute del historial
        val singleTop: Boolean = false     // true = no apila dos copias de la misma pantalla
    ) : NavigationEvent()

    // Volver a la pantalla anterior (como el botón "atrás" del celular)
    data object PopBackStack : NavigationEvent()

    // Subir un nivel en la jerarquía de la app (en esta app equivale a PopBackStack)
    data object NavigateUp : NavigationEvent()
}
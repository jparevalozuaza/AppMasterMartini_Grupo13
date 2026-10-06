package cl.duoc.mastermartini.navigation

sealed class Screen(
    val route: String,
    val titulo: String
) {

    data object Home : Screen(route = "home", titulo = "Inicio")
    data object Catalogo : Screen(route = "catalogo", titulo = "Catálogo")
    data object Perfil : Screen(route = "perfil", titulo = "Perfil")
}
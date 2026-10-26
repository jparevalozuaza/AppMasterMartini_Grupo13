package cl.duoc.mastermartini.navigation
sealed class Screen(
    val route: String,
    val titulo: String
) {
    data object Home : Screen(
        route = "home",
        titulo = "Inicio"
    )

    data object Catalogo : Screen(
        route = "catalogo",
        titulo = "Catálogo"
    )

    data object Aprender : Screen(
        route = "aprender",
        titulo = "Aprender"
    )

    data object Favoritos : Screen(
        route = "favoritos",
        titulo = "Favoritos"
    )
    data object Perfil : Screen(
        route = "perfil",
        titulo = "Perfil"
    )

    data object Detalle : Screen(
        route = "detalle/{contenidoId}",
        titulo = "Detalle"
    ) {
        fun crearRuta(contenidoId: Int): String {
            return "detalle/$contenidoId"
        }
    }

}

package cl.duoc.mastermartini.navigation

sealed class Screen(
    val route: String,
    val titulo: String
) {

    // Pantallas de acceso (Integrante 1)
    data object Login : Screen(
        route = "login",
        titulo = "Ingresar"
    )

    data object Registro : Screen(
        route = "registro",
        titulo = "Crear cuenta"
    )

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

    data object Cursos : Screen(
        route = "cursos",
        titulo = "Cursos"
    )

    data object DetalleCurso : Screen(
        route = "detalleCurso/{cursoId}",
        titulo = "Detalle del curso"
    ) {
        fun crearRuta(cursoId: Int): String {
            return "detalleCurso/$cursoId"
        }
    }

    data object Leccion : Screen(
        route = "leccion/{leccionId}",
        titulo = "Lección"
    ) {
        fun crearRuta(leccionId: Int): String {
            return "leccion/$leccionId"
        }
    }



    data object Favoritos : Screen(
        route = "favoritos",
        titulo = "Favoritos"
    )

    data object Recetas : Screen(
        route = "recetas",
        titulo = "Recetas"
    )

    data object DetalleReceta : Screen(
        route = "detalleReceta/{recetaId}",
        titulo = "Detalle de receta"
    ) {
        fun crearRuta(recetaId: Int): String {
            return "detalleReceta/$recetaId"
        }
    }

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

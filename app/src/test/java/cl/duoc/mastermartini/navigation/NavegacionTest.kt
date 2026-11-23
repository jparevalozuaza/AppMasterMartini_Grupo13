package cl.duoc.mastermartini.navigation

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE LA NAVEGACIÓN: rutas de cada pantalla y eventos
// ─────────────────────────────────────────────────────────────
class NavegacionTest : StringSpec({

    "todas las pantallas tienen un título y ninguna ruta se repite" {
        val pantallas = listOf(
            Screen.Login, Screen.Registro, Screen.Home, Screen.Catalogo, Screen.Aprender,
            Screen.Cursos, Screen.DetalleCurso, Screen.Leccion, Screen.Favoritos, Screen.Recetas,
            Screen.DetalleReceta, Screen.RecetasExternas, Screen.FormularioReceta, Screen.Perfil,
            Screen.Detalle
        )

        pantallas.all { it.titulo.isNotBlank() } shouldBe true
        pantallas.map { it.route }.toSet().size shouldBe pantallas.size
    }

    "las rutas con parámetro reemplazan el id dentro de la ruta" {
        Screen.DetalleCurso.crearRuta(2) shouldBe "detalleCurso/2"
        Screen.Leccion.crearRuta(101) shouldBe "leccion/101"
        Screen.DetalleReceta.crearRuta(5) shouldBe "detalleReceta/5"
        Screen.Detalle.crearRuta(3) shouldBe "detalle/3"
    }

    "el formulario de recetas usa 0 para crear y el id de la receta para editar" {
        Screen.FormularioReceta.crearRuta() shouldBe "formularioReceta/0"
        Screen.FormularioReceta.crearRuta(8) shouldBe "formularioReceta/8"
    }

    "un evento NavigateTo, por defecto, no borra el historial" {
        val evento = NavigationEvent.NavigateTo(Screen.Recetas)

        evento.popUpToRoute.shouldBeNull()
        evento.inclusive shouldBe false
        evento.singleTop shouldBe false
    }
})

package cl.duoc.mastermartini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.mastermartini.navigation.NavigationEvent
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.data.repository.ContenidoRepository
import cl.duoc.mastermartini.data.repository.CursoRepository
import cl.duoc.mastermartini.ui.screens.AprenderScreen
import cl.duoc.mastermartini.ui.screens.CatalogoScreen
import cl.duoc.mastermartini.ui.screens.CursosScreen
import cl.duoc.mastermartini.ui.screens.DetalleCursoScreen
import cl.duoc.mastermartini.ui.screens.DetalleRecetaScreen
import cl.duoc.mastermartini.ui.screens.DetalleScreen
import cl.duoc.mastermartini.ui.screens.FavoritosScreen
import cl.duoc.mastermartini.ui.screens.HomeScreen
import cl.duoc.mastermartini.ui.screens.LeccionScreen
import cl.duoc.mastermartini.ui.screens.LoginScreen
import cl.duoc.mastermartini.ui.screens.PerfilScreen
import cl.duoc.mastermartini.ui.screens.RecetaFormScreen
import cl.duoc.mastermartini.ui.screens.RecetasScreen
import cl.duoc.mastermartini.ui.screens.RegistroScreen
import cl.duoc.mastermartini.ui.theme.AppMasterMartini_Grupo13Theme
import cl.duoc.mastermartini.viewmodel.AuthViewModel
import cl.duoc.mastermartini.viewmodel.MainViewModel
import cl.duoc.mastermartini.viewmodel.PerfilViewModel
import cl.duoc.mastermartini.viewmodel.RecetaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppMasterMartini_Grupo13Theme {
                val viewModel: MainViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                val perfilViewModel: PerfilViewModel = viewModel()
                // Un solo ViewModel de recetas para la lista, el detalle y el formulario:
                // así, al crear, editar o eliminar, todas las pantallas ven los mismos datos.
                val recetaViewModel: RecetaViewModel = viewModel()

                // Preferencias de accesibilidad guardadas en DataStore.
                val textoGrande by perfilViewModel.textoGrande.collectAsState()
                val reducirMovimiento by perfilViewModel.reducirMovimiento.collectAsState()

                // Aplica el aumento de tamaño de texto a todas las pantallas.
                val densidadActual = LocalDensity.current
                val densidadApp = Density(
                    density = densidadActual.density,
                    fontScale = densidadActual.fontScale * (if (textoGrande) 1.25f else 1f)
                )

                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                // La barra inferior aparece solo en las secciones principales.
                val mostrarBarra = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Aprender.route,
                    Screen.Cursos.route,
                    Screen.Favoritos.route,
                    Screen.Perfil.route,
                    Screen.Catalogo.route,
                    Screen.Recetas.route
                )

                // Escucha los eventos de navegación emitidos por el ViewModel.
                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collect { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.route.route) {
                                    event.popUpToRoute?.let { destino ->
                                        popUpTo(destino.route) {
                                            inclusive = event.inclusive
                                        }
                                    }
                                    launchSingleTop = event.singleTop
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                            is NavigationEvent.NavigateToRoute -> navController.navigate(event.route)
                        }
                    }
                }

                // Proporciona la escala de texto accesible a toda la aplicación.
                CompositionLocalProvider(LocalDensity provides densidadApp) {
                    Scaffold(
                        bottomBar = {
                            if (mostrarBarra) {
                                NavigationBar {
                                    // Una sola definición para las 5 pestañas (antes eran 5 bloques casi idénticos)
                                    seccionesBarra.forEach { seccion ->
                                        NavigationBarItem(
                                            selected = currentRoute == seccion.pantalla.route,
                                            onClick = {
                                                navController.navigate(seccion.pantalla.route) {
                                                    // Inicio queda siempre como base del historial: así "atrás"
                                                    // vuelve a Inicio y "Cerrar sesión" puede limpiar todo.
                                                    popUpTo(Screen.Home.route) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true   // no abre dos copias de la misma pestaña
                                                    restoreState = true      // recupera el estado al volver a la pestaña
                                                }
                                            },
                                            icon = { Icon(seccion.icono, contentDescription = seccion.etiqueta) },
                                            label = { Text(seccion.etiqueta) }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Login.route,
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            // Transiciones animadas, respetando la preferencia de reducir movimiento.
                            enterTransition = {
                                if (reducirMovimiento) EnterTransition.None
                                else fadeIn(animationSpec = tween(300)) +
                                        slideInHorizontally(animationSpec = tween(300)) { ancho -> ancho / 4 }
                            },
                            exitTransition = {
                                if (reducirMovimiento) ExitTransition.None
                                else fadeOut(animationSpec = tween(300))
                            },
                            popEnterTransition = {
                                if (reducirMovimiento) EnterTransition.None
                                else fadeIn(animationSpec = tween(300))
                            },
                            popExitTransition = {
                                if (reducirMovimiento) ExitTransition.None
                                else fadeOut(animationSpec = tween(300)) +
                                        slideOutHorizontally(animationSpec = tween(300)) { ancho -> ancho / 4 }
                            }
                        ) {
                            // Acceso y registro.
                            composable(Screen.Login.route) {
                                LoginScreen(authViewModel = authViewModel, mainViewModel = viewModel)
                            }
                            composable(Screen.Registro.route) {
                                RegistroScreen(authViewModel = authViewModel, mainViewModel = viewModel)
                            }

                            // Secciones principales.
                            composable(Screen.Home.route) { HomeScreen(viewModel = viewModel) }
                            composable(Screen.Catalogo.route) { CatalogoScreen(viewModel = viewModel) }
                            composable(Screen.Perfil.route) {
                                PerfilScreen(mainViewModel = viewModel, perfilViewModel = perfilViewModel)
                            }
                            composable(Screen.Aprender.route) { AprenderScreen(viewModel = viewModel) }
                            composable(Screen.Favoritos.route) { FavoritosScreen(viewModel = viewModel) }
                            composable(Screen.Cursos.route) { CursosScreen(viewModel = viewModel) }

                            // Detalle del curso.
                            composable(
                                route = Screen.DetalleCurso.route,
                                arguments = listOf(navArgument("cursoId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val cursoId = backStackEntry.arguments?.getInt("cursoId") ?: -1
                                val curso = CursoRepository.cursos.find { it.id == cursoId }
                                if (curso != null) {
                                    DetalleCursoScreen(
                                        curso = curso,
                                        onAbrirLeccion = { leccionId ->
                                            navController.navigate(Screen.Leccion.crearRuta(leccionId))
                                        },
                                        onVolver = { navController.popBackStack() }
                                    )
                                } else {
                                    Text("No se encontró el curso.")
                                }
                            }

                            // Contenido de la lección.
                            composable(
                                route = Screen.Leccion.route,
                                arguments = listOf(navArgument("leccionId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val leccionId = backStackEntry.arguments?.getInt("leccionId") ?: -1
                                val leccion = CursoRepository.cursos
                                    .flatMap { it.lecciones }
                                    .find { it.id == leccionId }
                                if (leccion != null) {
                                    LeccionScreen(
                                        leccion = leccion,
                                        onVolver = { navController.popBackStack() }
                                    )
                                } else {
                                    Text("No se encontró la lección.")
                                }
                            }

                            // Detalle del contenido de aprendizaje.
                            composable(
                                route = Screen.Detalle.route,
                                arguments = listOf(navArgument("contenidoId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val contenidoId = backStackEntry.arguments?.getInt("contenidoId")
                                val contenido = ContenidoRepository.contenidos.find { it.id == contenidoId }
                                DetalleScreen(contenido = contenido, viewModel = viewModel)
                            }

                            // Recetas: lista, detalle y formulario (comparten recetaViewModel).
                            composable(Screen.Recetas.route) {
                                RecetasScreen(viewModel = recetaViewModel, navController = navController)
                            }
                            composable(
                                route = Screen.DetalleReceta.route,
                                arguments = listOf(navArgument("recetaId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val recetaId = backStackEntry.arguments?.getInt("recetaId") ?: 0
                                DetalleRecetaScreen(
                                    recetaId = recetaId,
                                    viewModel = recetaViewModel,
                                    onVolver = { navController.popBackStack() },
                                    onEditar = { id ->
                                        navController.navigate(Screen.FormularioReceta.crearRuta(id))
                                    },
                                    onEliminada = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = Screen.FormularioReceta.route,
                                arguments = listOf(navArgument("recetaId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val recetaId = backStackEntry.arguments?.getInt("recetaId") ?: 0
                                RecetaFormScreen(
                                    recetaId = recetaId,
                                    viewModel = recetaViewModel,
                                    onVolver = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// PESTAÑAS DE LA BARRA INFERIOR
// Cada pestaña tiene una pantalla de destino, un texto y un ícono.
// Para agregar o quitar una pestaña basta con editar esta lista.
// ─────────────────────────────────────────────────────────────
private data class SeccionBarra(
    val pantalla: Screen,
    val etiqueta: String,
    val icono: ImageVector
)

private val seccionesBarra = listOf(
    SeccionBarra(Screen.Home, "Inicio", Icons.Filled.Home),
    SeccionBarra(Screen.Aprender, "Aprender", Icons.AutoMirrored.Filled.List),
    SeccionBarra(Screen.Cursos, "Cursos", Icons.Filled.Info),
    SeccionBarra(Screen.Favoritos, "Favoritos", Icons.Filled.Favorite),
    SeccionBarra(Screen.Perfil, "Perfil", Icons.Filled.Person)
)

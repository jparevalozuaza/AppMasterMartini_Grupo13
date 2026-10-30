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
import androidx.compose.material.icons.filled.List
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.mastermartini.navigation.NavigationEvent
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.repository.ContenidoRepository
import cl.duoc.mastermartini.repository.CursoRepository
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
                                    NavigationBarItem(
                                        selected = currentRoute == Screen.Home.route,
                                        onClick = {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                                        label = { Text("Inicio") }
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == Screen.Aprender.route,
                                        onClick = {
                                            navController.navigate(Screen.Aprender.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(Icons.Filled.List, contentDescription = "Aprender") },
                                        label = { Text("Aprender") }
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == Screen.Cursos.route,
                                        onClick = {
                                            navController.navigate(Screen.Cursos.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(Icons.Filled.Info, contentDescription = "Cursos") },
                                        label = { Text("Cursos") }
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == Screen.Favoritos.route,
                                        onClick = {
                                            navController.navigate(Screen.Favoritos.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoritos") },
                                        label = { Text("Favoritos") }
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == Screen.Perfil.route,
                                        onClick = {
                                            navController.navigate(Screen.Perfil.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                                        label = { Text("Perfil") }
                                    )
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

                            // Recetas y detalle de receta.
                            composable(Screen.Recetas.route) {
                                val recetaViewModel: RecetaViewModel = viewModel()
                                RecetasScreen(viewModel = recetaViewModel, navController = navController)
                            }
                            composable(
                                route = Screen.DetalleReceta.route,
                                arguments = listOf(navArgument("recetaId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val recetaId = backStackEntry.arguments?.getInt("recetaId") ?: 0
                                val recetaViewModel: RecetaViewModel = viewModel()
                                DetalleRecetaScreen(
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

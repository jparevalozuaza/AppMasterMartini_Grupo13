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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.mastermartini.navigation.NavigationEvent
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.ui.screens.CatalogoScreen
import cl.duoc.mastermartini.ui.screens.HomeScreen
import cl.duoc.mastermartini.ui.screens.LoginScreen
import cl.duoc.mastermartini.ui.screens.RegistroScreen
import cl.duoc.mastermartini.viewmodel.AuthViewModel
import cl.duoc.mastermartini.ui.screens.PerfilScreen
import cl.duoc.mastermartini.ui.theme.AppMasterMartini_Grupo13Theme
import cl.duoc.mastermartini.viewmodel.MainViewModel
import cl.duoc.mastermartini.viewmodel.PerfilViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cl.duoc.mastermartini.repository.ContenidoRepository
import cl.duoc.mastermartini.ui.screens.DetalleScreen
import cl.duoc.mastermartini.ui.screens.AprenderScreen
import cl.duoc.mastermartini.ui.screens.FavoritosScreen
import cl.duoc.mastermartini.ui.screens.RecetasScreen
import cl.duoc.mastermartini.viewmodel.RecetaViewModel
import cl.duoc.mastermartini.ui.screens.DetalleRecetaScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMasterMartini_Grupo13Theme {

                // ViewModels compartidos, creados una sola vez para toda la app
                val viewModel: MainViewModel = viewModel()          // navegación
                val authViewModel: AuthViewModel = viewModel()      // Login y Registro
                val perfilViewModel: PerfilViewModel = viewModel()  // Perfil y preferencias

                // ACCESIBILIDAD (preferencias guardadas en DataStore y elegidas en Perfil)
                val textoGrande by perfilViewModel.textoGrande.collectAsState()
                val reducirMovimiento by perfilViewModel.reducirMovimiento.collectAsState()
                // Texto grande: todas las letras de la app crecen un 25 %
                val densidadActual = LocalDensity.current
                val densidadApp = Density(
                    density = densidadActual.density,
                    fontScale = densidadActual.fontScale * (if (textoGrande) 1.25f else 1f)
                )
                // NavController = el "conductor" que cambia de pantalla y lleva el historial
                val navController = rememberNavController()

                // LAUNCHEDEFFECT: se ejecuta una vez al iniciar y se queda ESCUCHANDO los eventos
                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collect { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.route.route) {
                                    // Si el evento indica popUpTo, se limpia el historial hasta esa pantalla
                                    event.popUpToRoute?.let { destino ->
                                        popUpTo(destino.route) { inclusive = event.inclusive }
                                    }
                                    launchSingleTop = event.singleTop
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                            is NavigationEvent.NavigateToRoute -> {
                                navController.navigate(event.route)
                            }
                        }
                    }
                }

                // CompositionLocalProvider: entrega la escala de texto a TODAS las pantallas
                CompositionLocalProvider(LocalDensity provides densidadApp) {
                    // NAVHOST = el "escenario": muestra la pantalla que corresponde a la ruta actual
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,  // la app ahora inicia en Login
                        modifier = Modifier.fillMaxSize(),
                        // ANIMACIÓN: transiciones entre pantallas (300 ms).
                        // Si el usuario activó "Reducir movimiento", se cambia de pantalla sin animación.
                        // Al avanzar: la nueva pantalla aparece desvaneciéndose y deslizándose un poco desde la derecha
                        enterTransition = {
                            if (reducirMovimiento) EnterTransition.None
                            else fadeIn(animationSpec = tween(300)) +
                                slideInHorizontally(animationSpec = tween(300)) { ancho -> ancho / 4 }
                        },
                        exitTransition = {
                            if (reducirMovimiento) ExitTransition.None
                            else fadeOut(animationSpec = tween(300))
                        },
                        // Al volver atrás: el movimiento es al revés
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
                        // Pantallas de acceso (Integrante 1)
                        composable(Screen.Login.route) {
                            LoginScreen(authViewModel = authViewModel, mainViewModel = viewModel)
                        }
                        composable(Screen.Registro.route) {
                            RegistroScreen(authViewModel = authViewModel, mainViewModel = viewModel)
                        }

                        composable(Screen.Home.route) { HomeScreen(viewModel = viewModel) }
                        composable(Screen.Catalogo.route) { CatalogoScreen(viewModel = viewModel) }
                        composable(Screen.Perfil.route) {
                            PerfilScreen(mainViewModel = viewModel, perfilViewModel = perfilViewModel)
                        }
                        composable(
                            route = Screen.Detalle.route,
                            arguments = listOf(
                                navArgument("contenidoId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { backStackEntry ->

                            val contenidoId = backStackEntry.arguments
                                ?.getInt("contenidoId")

                            val contenido = ContenidoRepository.contenidos.find {
                                it.id == contenidoId
                            }

                            DetalleScreen(
                                contenido = contenido,
                                viewModel = viewModel
                            )
                        }
                        composable(Screen.Aprender.route) { AprenderScreen(viewModel = viewModel) }
                        composable(Screen.Favoritos.route) { FavoritosScreen(viewModel = viewModel) }
                        composable(Screen.Recetas.route) {
                            val recetaViewModel: RecetaViewModel = viewModel()

                            RecetasScreen(
                                viewModel = recetaViewModel,
                                navController = navController
                            )
                        }

                        composable(
                            route = Screen.DetalleReceta.route,
                            arguments = listOf(
                                navArgument("recetaId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { backStackEntry ->

                            val recetaId = backStackEntry.arguments?.getInt("recetaId") ?: 0
                            val recetaViewModel: RecetaViewModel = viewModel()

                            DetalleRecetaScreen(
                                recetaId = recetaId,
                                viewModel = recetaViewModel,
                                onVolver = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                } // fin CompositionLocalProvider
            }
        }
    }
}
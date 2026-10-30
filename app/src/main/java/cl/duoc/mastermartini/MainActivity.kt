package cl.duoc.mastermartini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import cl.duoc.mastermartini.viewmodel.RecetaViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppMasterMartini_Grupo13Theme {

                val viewModel: MainViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                val navController = rememberNavController()

                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                // Mostrar la barra únicamente en las secciones principales.
                val mostrarBarra = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Aprender.route,
                    Screen.Cursos.route,
                    Screen.Favoritos.route,
                    Screen.Perfil.route,
                    Screen.Catalogo.route,
                    Screen.Recetas.route
                )

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

                            is NavigationEvent.PopBackStack ->
                                navController.popBackStack()

                            is NavigationEvent.NavigateUp ->
                                navController.navigateUp()

                            is NavigationEvent.NavigateToRoute -> {
                                navController.navigate(event.route)
                            }
                        }
                    }
                }

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
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.Home,
                                            contentDescription = "Inicio"
                                        )
                                    },
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
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.List,
                                            contentDescription = "Aprender"
                                        )
                                    },
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
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.Info,
                                            contentDescription = "Cursos"
                                        )
                                    },
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
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = "Favoritos"
                                        )
                                    },
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
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = "Perfil"
                                        )
                                    },
                                    label = { Text("Perfil") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        // Acceso
                        composable(Screen.Login.route) {
                            LoginScreen(
                                authViewModel = authViewModel,
                                mainViewModel = viewModel
                            )
                        }

                        composable(Screen.Registro.route) {
                            RegistroScreen(
                                authViewModel = authViewModel,
                                mainViewModel = viewModel
                            )
                        }

                        // Pantallas principales
                        composable(Screen.Home.route) {
                            HomeScreen(viewModel = viewModel)
                        }

                        composable(Screen.Catalogo.route) {
                            CatalogoScreen(viewModel = viewModel)
                        }

                        composable(Screen.Perfil.route) {
                            PerfilScreen(viewModel = viewModel)
                        }

                        composable(Screen.Aprender.route) {
                            AprenderScreen(viewModel = viewModel)
                        }

                        composable(Screen.Favoritos.route) {
                            FavoritosScreen(viewModel = viewModel)
                        }

                        // Listado de cursos
                        composable(Screen.Cursos.route) {
                            CursosScreen(viewModel = viewModel)
                        }

                        // Detalle del curso
                        composable(
                            route = Screen.DetalleCurso.route,
                            arguments = listOf(
                                navArgument("cursoId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { backStackEntry ->

                            val cursoId =
                                backStackEntry.arguments?.getInt("cursoId") ?: -1

                            val curso = CursoRepository.cursos.find {
                                it.id == cursoId
                            }

                            if (curso != null) {
                                DetalleCursoScreen(
                                    curso = curso,
                                    onAbrirLeccion = { leccionId ->
                                        navController.navigate(
                                            Screen.Leccion.crearRuta(leccionId)
                                        )
                                    },
                                    onVolver = {
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                Text("No se encontró el curso.")
                            }
                        }

                        // Contenido de la lección
                        composable(
                            route = Screen.Leccion.route,
                            arguments = listOf(
                                navArgument("leccionId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { backStackEntry ->

                            val leccionId =
                                backStackEntry.arguments?.getInt("leccionId") ?: -1

                            val leccion = CursoRepository.cursos
                                .flatMap { it.lecciones }
                                .find { it.id == leccionId }

                            if (leccion != null) {
                                LeccionScreen(
                                    leccion = leccion,
                                    onVolver = {
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                Text("No se encontró la lección.")
                            }
                        }

                        // Detalle del contenido de aprendizaje
                        composable(
                            route = Screen.Detalle.route,
                            arguments = listOf(
                                navArgument("contenidoId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { backStackEntry ->

                            val contenidoId =
                                backStackEntry.arguments?.getInt("contenidoId")

                            val contenido = ContenidoRepository.contenidos.find {
                                it.id == contenidoId
                            }

                            DetalleScreen(
                                contenido = contenido,
                                viewModel = viewModel
                            )
                        }

                        // Recetas
                        composable(Screen.Recetas.route) {
                            val recetaViewModel: RecetaViewModel = viewModel()

                            RecetasScreen(
                                viewModel = recetaViewModel,
                                navController = navController
                            )
                        }

                        // Detalle de receta
                        composable(
                            route = Screen.DetalleReceta.route,
                            arguments = listOf(
                                navArgument("recetaId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { backStackEntry ->

                            val recetaId =
                                backStackEntry.arguments?.getInt("recetaId") ?: 0

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
                }
            }
        }
    }
}

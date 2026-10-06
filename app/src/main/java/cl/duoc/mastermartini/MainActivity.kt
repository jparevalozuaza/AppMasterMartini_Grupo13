package cl.duoc.mastermartini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.mastermartini.navigation.NavigationEvent
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.ui.screens.CatalogoScreen
import cl.duoc.mastermartini.ui.screens.HomeScreen
import cl.duoc.mastermartini.ui.screens.PerfilScreen
import cl.duoc.mastermartini.ui.theme.AppMasterMartini_Grupo13Theme
import cl.duoc.mastermartini.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMasterMartini_Grupo13Theme {

                // UN SOLO ViewModel para toda la app, creado aquí y compartido con todas las pantallas
                val viewModel: MainViewModel = viewModel()
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
                        }
                    }
                }

                // NAVHOST = el "escenario": muestra la pantalla que corresponde a la ruta actual
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,  // pantalla inicial
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Home.route) { HomeScreen(viewModel = viewModel) }
                    composable(Screen.Catalogo.route) { CatalogoScreen(viewModel = viewModel) }
                    composable(Screen.Perfil.route) { PerfilScreen(viewModel = viewModel) }
                }
            }
        }
    }
}
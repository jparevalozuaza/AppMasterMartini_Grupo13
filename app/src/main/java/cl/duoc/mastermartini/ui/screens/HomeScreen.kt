package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.ui.utils.obtenerWindowSizeClass
import cl.duoc.mastermartini.viewmodel.MainViewModel
import kotlinx.coroutines.launch

// Pantalla de Inicio: TopAppBar + menú lateral + contenido adaptativo (Guía 9)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {

    // Estado del menú lateral: empieza cerrado
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // "Scope" para lanzar la animación de abrir/cerrar el menú
    val scope = rememberCoroutineScope()
    // Tamaño de pantalla (Compact / Medium / Expanded), calculado con la utilidad de la Guía 9
    val windowSizeClass = obtenerWindowSizeClass()

    // MODALNAVIGATIONDRAWER: menú que se desliza desde la izquierda sobre la pantalla
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Contenido del menú
            ModalDrawerSheet {
                Text(
                    text = "Menú",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                NavigationDrawerItem(
                    label = { Text(Screen.Catalogo.titulo) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }  // 1) cerrar el menú
                        viewModel.navigateTo(Screen.Catalogo) // 2) pedir navegación al ViewModel
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text(Screen.Perfil.titulo) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.navigateTo(Screen.Perfil)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        // Contenido principal (lo que se ve cuando el menú está cerrado)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Escuela Martini") },
                    // Botón ☰ a la izquierda que abre el menú
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            // Box respeta el espacio de la barra; dentro va la variante según el tamaño de pantalla
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> HomeScreenCompacta()
                    WindowWidthSizeClass.Medium -> HomeScreenMediana()
                    WindowWidthSizeClass.Expanded -> HomeScreenExpandida()
                }
            }
        }
    }
}
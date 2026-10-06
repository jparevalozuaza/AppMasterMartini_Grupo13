package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.viewmodel.MainViewModel

// Pantalla de Catálogo: muestra la BOTTOMBAR (barra de navegación inferior)
@Composable
fun CatalogoScreen(viewModel: MainViewModel) {

    // Pestañas de la barra inferior
    val pestanas = listOf(Screen.Home, Screen.Catalogo, Screen.Perfil)
    // Categorías de ejemplo (datos ficticios, como exige el caso)
    val categorias = listOf("Chocolatería", "Pastelería", "Panadería", "Heladería")

    Scaffold(
        bottomBar = {
            NavigationBar {
                // forEach = repetir lo siguiente por cada pestaña de la lista
                pestanas.forEach { screen ->
                    NavigationBarItem(
                        // Marcada solo la pestaña de esta pantalla
                        selected = screen == Screen.Catalogo,
                        onClick = { viewModel.navigateTo(screen) },
                        label = { Text(screen.titulo) },
                        icon = {
                            Icon(
                                // when sobre una sealed class: Kotlin exige cubrir TODAS las opciones
                                imageVector = when (screen) {
                                    Screen.Home -> Icons.Filled.Home
                                    Screen.Catalogo -> Icons.Filled.Star
                                    Screen.Perfil -> Icons.Filled.Person
                                },
                                contentDescription = screen.titulo
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Catálogo", style = MaterialTheme.typography.headlineMedium)
            Text("Elige una categoría", style = MaterialTheme.typography.bodyLarge)
            // Un botón grande por categoría (accesibilidad: fácil de tocar y leer)
            categorias.forEach { categoria ->
                OutlinedButton(
                    onClick = { /* Más adelante: abrir la lista de esa categoría */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(categoria, fontSize = 18.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogoScreenPreview() {
    CatalogoScreen(viewModel = MainViewModel())
}
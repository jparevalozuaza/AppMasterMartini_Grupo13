package cl.duoc.mastermartini.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.viewmodel.MainViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun CatalogoScreen(viewModel: MainViewModel) {

    val pestanas = listOf(
        Screen.Home,
        Screen.Catalogo,
        Screen.Perfil
    )

    val categorias = listOf(
        "Chocolatería",
        "Pastelería",
        "Panadería",
        "Heladería"
    )

    var categoriaSeleccionada by remember {
        mutableStateOf<String?>(null)
    }

    val contenidos = when (categoriaSeleccionada) {
        "Chocolatería" -> listOf(
            "Introducción a la chocolatería",
            "Técnicas de templado",
            "Elaboración de bombones"
        )

        "Pastelería" -> listOf(
            "Pastelería de vitrina",
            "Preparación de cremas",
            "Decoración de tortas"
        )

        "Panadería" -> listOf(
            "Técnicas de panadería",
            "Preparación de masas",
            "Fermentación de masas"
        )

        "Heladería" -> listOf(
            "Introducción a la heladería",
            "Elaboración de helados",
            "Texturas y sabores"
        )

        else -> emptyList()
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                pestanas.forEach { screen ->
                    NavigationBarItem(
                        selected = screen == Screen.Catalogo,
                        onClick = {
                            viewModel.navigateTo(screen)
                        },
                        label = {
                            Text(screen.titulo)
                        },
                        icon = {
                            androidx.compose.material3.Icon(
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Catálogo",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Elige una categoría",
                style = MaterialTheme.typography.bodyLarge
            )

            categorias.forEach { categoria ->
                OutlinedButton(
                    onClick = {
                        categoriaSeleccionada = categoria
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = categoria,
                        fontSize = 18.sp
                    )
                }
            }

            if (categoriaSeleccionada == null) {

                Text(
                    text = "Selecciona una categoría para explorar " +
                            "nuestros contenidos."
                )

            } else {

                Text(
                    text = "Contenidos de $categoriaSeleccionada",
                    style = MaterialTheme.typography.titleLarge
                )

                contenidos.forEach { contenido ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = contenido,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Descubre técnicas y conocimientos " +
                                        "para desarrollar tus habilidades."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogoScreenPreview() {
    CatalogoScreen(viewModel = MainViewModel())
}

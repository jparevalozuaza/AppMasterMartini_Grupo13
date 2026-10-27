package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.viewmodel.RecetaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetasScreen(
    viewModel: RecetaViewModel,
    navController: NavController
) {
    val recetas by viewModel.recetas.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }

    val categorias = listOf("Todas") +
            recetas.map { it.categoria }.distinct().sorted()

    val recetasFiltradas = recetas.filter { receta ->
        val coincideTexto =
            receta.titulo.contains(textoBusqueda, ignoreCase = true) ||
                    receta.descripcion.contains(textoBusqueda, ignoreCase = true)

        val coincideCategoria =
            categoriaSeleccionada == "Todas" ||
                    receta.categoria == categoriaSeleccionada

        coincideTexto && coincideCategoria
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recetas") }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Buscar recetas") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.horizontalScroll(
                    rememberScrollState()
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { categoria ->
                    FilterChip(
                        selected = categoriaSeleccionada == categoria,
                        onClick = {
                            categoriaSeleccionada = categoria
                        },
                        label = { Text(categoria) }
                    )
                }
            }

            if (recetas.isEmpty()) {
                Text("Cargando recetas...")
            } else if (recetasFiltradas.isEmpty()) {
                Text(
                    text = "No se encontraron recetas.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recetasFiltradas) { receta ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(
                                        Screen.DetalleReceta.crearRuta(
                                            receta.id
                                        )
                                    )
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = receta.categoria,
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Text(
                                    text = receta.titulo,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = receta.descripcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "Preparación: ${receta.tiempoPreparacion} minutos",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

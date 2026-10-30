package cl.duoc.mastermartini.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.repository.ContenidoRepository
import cl.duoc.mastermartini.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AprenderScreen(viewModel: MainViewModel) {

    val contenidos = ContenidoRepository.contenidos

    val contenidosCompletados = viewModel.progreso.value

    val cantidadCompletada = contenidos.count {
        it.id in contenidosCompletados
    }

    val porcentaje = if (contenidos.isNotEmpty()) {
        cantidadCompletada * 100 / contenidos.size
    } else {
        0
    }

    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }

    val categorias = listOf("Todas") +
            contenidos.map { it.categoria }.distinct()

    val contenidosFiltrados = contenidos.filter { contenido ->

        val coincideTexto =
            contenido.titulo.contains(textoBusqueda, ignoreCase = true) ||
                    contenido.descripcion.contains(textoBusqueda, ignoreCase = true) ||
                    contenido.categoria.contains(textoBusqueda, ignoreCase = true)

        val coincideCategoria =
            categoriaSeleccionada == "Todas" ||
                    contenido.categoria == categoriaSeleccionada

        coincideTexto && coincideCategoria
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Aprender")
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text(
                    text = "Explora y aprende",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Descubre técnicas y contenidos de gastronomía.",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    label = {
                        Text("Buscar contenido")
                    },
                    placeholder = {
                        Text("Ej.: chocolate, pan, heladería")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    trailingIcon = {
                        if (textoBusqueda.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    textoBusqueda = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar búsqueda"
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Filtrar por categoría",
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
                                label = {
                                    Text(categoria)
                                }
                            )
                        }
                    }

                    if (contenidosFiltrados.isNotEmpty()) {
                        Text(
                            text = if (contenidosFiltrados.size == 1) {
                                "1 contenido encontrado"
                            } else {
                                "${contenidosFiltrados.size} contenidos encontrados"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Tu progreso de aprendizaje",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "$cantidadCompletada de ${contenidos.size} contenidos completados",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        LinearProgressIndicator(
                            progress = { porcentaje / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "$porcentaje% completado",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (contenidosFiltrados.isEmpty()) {
                item {
                    Text(
                        text = "No se encontraron contenidos. Prueba otra búsqueda o categoría.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            items(
                items = contenidosFiltrados,
                key = { contenido -> contenido.id }
            ) { contenido ->

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 300)
                    ) + expandVertically(
                        animationSpec = tween(durationMillis = 300)
                    ),
                    exit = fadeOut(
                        animationSpec = tween(durationMillis = 200)
                    ) + shrinkVertically(
                        animationSpec = tween(durationMillis = 200)
                    )
                ) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.navigateToRoute(
                                    Screen.Detalle.crearRuta(contenido.id)
                                )
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = contenido.categoria,
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                text = contenido.titulo,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = contenido.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "Ver contenido →",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

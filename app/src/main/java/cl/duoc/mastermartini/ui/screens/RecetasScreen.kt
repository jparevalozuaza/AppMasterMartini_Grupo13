package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.mastermartini.model.Receta
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.ui.state.RecetaUiState
import cl.duoc.mastermartini.viewmodel.RecetaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetasScreen(
    viewModel: RecetaViewModel,
    navController: NavController,
    esAdministrador: Boolean        // true: muestra el botón "+" para crear recetas
) {
    // Estado que entrega el ViewModel: Cargando, Exito o Error
    val estado by viewModel.estado.collectAsState()
    val recetas = (estado as? RecetaUiState.Exito)?.recetas ?: emptyList()

    // Mensaje de confirmación ("Receta creada", etc.) que deja el ViewModel
    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Cada vez que llega un mensaje nuevo se muestra en el Snackbar y luego se "consume"
    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumirMensaje()
        }
    }

    // Al entrar a la pantalla: si la carga anterior falló, se reintenta sola
    LaunchedEffect(Unit) {
        viewModel.cargarSiHayError()
    }

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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // Botón "+" para crear una receta nueva (abre el formulario vacío).
        // Solo lo ve un administrador; un usuario común solo consulta.
        floatingActionButton = {
            if (esAdministrador) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.FormularioReceta.crearRuta(0)) }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Crear receta")
                }
            }
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

            // Lo que se muestra depende del estado.
            // Como RecetaUiState es "sealed", el when debe cubrir los tres casos.
            when (val e = estado) {

                is RecetaUiState.Cargando -> MensajeCentrado {
                    CircularProgressIndicator()
                    Text("Cargando recetas…")
                }

                is RecetaUiState.Error -> MensajeCentrado {
                    Text(
                        text = e.mensaje,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = { viewModel.cargarRecetas() }) {
                        Text("Reintentar")
                    }
                }

                is RecetaUiState.Exito -> when {
                    recetas.isEmpty() -> MensajeCentrado {
                        Text(
                            text = "Aún no hay recetas.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    recetasFiltradas.isEmpty() -> Text(
                        text = "No se encontraron recetas.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    else -> LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        // Espacio al final para que el botón "+" no tape la última receta
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        // "key" ayuda a Compose a reconocer cada tarjeta cuando la lista cambia
                        items(recetasFiltradas, key = { it.id }) { receta ->
                            TarjetaReceta(
                                receta = receta,
                                onClick = {
                                    navController.navigate(
                                        Screen.DetalleReceta.crearRuta(receta.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Tarjeta de una receta en la lista
@Composable
private fun TarjetaReceta(receta: Receta, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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

// Contenido centrado en el espacio libre (carga, error o lista vacía).
// Es una extensión de ColumnScope para poder usar weight(1f) y ocupar el resto de la pantalla.
@Composable
private fun ColumnScope.MensajeCentrado(contenido: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            contenido()
        }
    }
}

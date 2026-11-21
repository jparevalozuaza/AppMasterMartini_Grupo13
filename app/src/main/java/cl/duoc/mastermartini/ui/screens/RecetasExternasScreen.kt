package cl.duoc.mastermartini.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.model.RecetaExterna
import cl.duoc.mastermartini.ui.components.MartiniTextField
import cl.duoc.mastermartini.ui.state.RecetaExternaUiState
import cl.duoc.mastermartini.viewmodel.RecetaExternaViewModel

// ─────────────────────────────────────────────────────────────
// "RECETAS DEL MUNDO": datos de la API externa DummyJSON (en inglés).
// Está separada de las recetas propias de Master Martini (otra pantalla,
// otro modelo, otro ViewModel y otro repositorio).
// Todos pueden consultar y buscar (GET); solo un administrador ve
// crear (POST), editar (PUT) y eliminar (DELETE).
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetasExternasScreen(
    viewModel: RecetaExternaViewModel,
    esAdministrador: Boolean,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val formulario by viewModel.formulario.collectAsState()

    // Receta que se quiere eliminar (null = no hay diálogo de confirmación abierto)
    var recetaAEliminar by remember { mutableStateOf<RecetaExterna?>(null) }

    // Mensajes de confirmación o error en el Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumirMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Recetas del mundo")
                        Text(
                            text = "Fuente: DummyJSON (API externa)",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (esAdministrador) {
                FloatingActionButton(onClick = { viewModel.abrirFormularioNueva() }) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar receta del mundo")
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Buscador: al tocar la lupa o "Buscar" en el teclado se hace un GET /recipes/search
            OutlinedTextField(
                value = busqueda,
                onValueChange = viewModel::onBusquedaChange,
                label = { Text("Buscar en inglés (ej.: pasta, cake)") },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.buscar() }) {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.buscar() }),
                modifier = Modifier.fillMaxWidth()
            )

            when (val e = estado) {

                is RecetaExternaUiState.Cargando -> CentroDePantalla {
                    CircularProgressIndicator()
                    Text("Consultando la API externa…")
                }

                is RecetaExternaUiState.Error -> CentroDePantalla {
                    Text(
                        text = e.mensaje,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = { viewModel.buscar() }) { Text("Reintentar") }
                }

                // Respuesta vacía: la API respondió bien, pero sin recetas
                is RecetaExternaUiState.Exito -> if (e.recetas.isEmpty()) {
                    CentroDePantalla {
                        Text(
                            text = "No se encontraron recetas para \"${busqueda.trim()}\".",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        TextButton(onClick = {
                            viewModel.onBusquedaChange("")
                            viewModel.cargarRecetas()
                        }) { Text("Ver todas las recetas") }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        items(e.recetas, key = { it.id }) { receta ->
                            TarjetaRecetaExterna(
                                receta = receta,
                                esAdministrador = esAdministrador,
                                onEditar = { viewModel.abrirFormularioEdicion(receta) },
                                onEliminar = { recetaAEliminar = receta }
                            )
                        }
                    }
                }
            }
        }
    }

    // Formulario para crear (POST) o editar (PUT)
    if (formulario.visible) {
        AlertDialog(
            onDismissRequest = { if (!formulario.guardando) viewModel.cerrarFormulario() },
            title = { Text(if (formulario.esEdicion) "Editar receta del mundo" else "Nueva receta del mundo") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MartiniTextField(
                        valor = formulario.nombre,
                        onValorChange = viewModel::onNombreChange,
                        etiqueta = "Nombre",
                        error = formulario.nombreError
                    )
                    MartiniTextField(
                        valor = formulario.cocina,
                        onValorChange = viewModel::onCocinaChange,
                        etiqueta = "Tipo de cocina (ej.: Italian)",
                        error = formulario.cocinaError
                    )
                    MartiniTextField(
                        valor = formulario.minutos,
                        onValorChange = viewModel::onMinutosChange,
                        etiqueta = "Minutos de preparación",
                        error = formulario.minutosError,
                        tipoTeclado = KeyboardType.Number
                    )
                    MartiniTextField(
                        valor = formulario.ingredientes,
                        onValorChange = viewModel::onIngredientesChange,
                        etiqueta = "Ingredientes (separados por coma)",
                        error = formulario.ingredientesError,
                        unaLinea = false,
                        lineasMinimas = 2,
                        imeAction = ImeAction.Default
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.guardarFormulario() },
                    enabled = !formulario.guardando
                ) { Text(if (formulario.guardando) "Enviando…" else "Guardar") }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cerrarFormulario() },
                    enabled = !formulario.guardando
                ) { Text("Cancelar") }
            }
        )
    }

    // Confirmación antes de eliminar (DELETE)
    recetaAEliminar?.let { receta ->
        AlertDialog(
            onDismissRequest = { recetaAEliminar = null },
            title = { Text("¿Eliminar receta?") },
            text = { Text("Se eliminará \"${receta.nombre}\" de la lista.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarReceta(receta)
                    recetaAEliminar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { recetaAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

// Tarjeta de una receta externa: al tocarla se despliegan ingredientes e instrucciones
@Composable
private fun TarjetaRecetaExterna(
    receta: RecetaExterna,
    esAdministrador: Boolean,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var expandida by rememberSaveable(receta.id) { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandida = !expandida }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = receta.nombre, style = MaterialTheme.typography.titleMedium)

            Text(
                text = listOf(
                    receta.cocina,
                    receta.dificultad,
                    "${receta.minutosPreparacion + receta.minutosCoccion} min"
                ).filter { it.isNotBlank() }.joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium
            )

            if (receta.valoracion > 0 || receta.calorias > 0) {
                Text(
                    text = "Valoración ${receta.valoracion} de 5 · ${receta.calorias} kcal por porción",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (receta.soloLocal) {
                Text(
                    text = "Creada en la app (DummyJSON no la guarda)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = if (expandida) "Ocultar detalle" else "Ver ingredientes e instrucciones",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            AnimatedVisibility(visible = expandida) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Ingredientes", style = MaterialTheme.typography.titleSmall)
                    receta.ingredientes.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }

                    Text("Instrucciones", style = MaterialTheme.typography.titleSmall)
                    if (receta.instrucciones.isEmpty()) {
                        Text("Sin instrucciones.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        receta.instrucciones.forEachIndexed { i, paso ->
                            Text("${i + 1}. $paso", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Acciones del CRUD: solo para administradores
            if (esAdministrador) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onEditar) { Text("Editar") }
                    TextButton(onClick = onEliminar) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

// Contenido centrado en el espacio libre (carga, error o respuesta vacía)
@Composable
private fun CentroDePantalla(contenido: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            contenido()
        }
    }
}

package cl.duoc.mastermartini.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.model.Receta
import cl.duoc.mastermartini.ui.state.RecetaUiState
import cl.duoc.mastermartini.viewmodel.RecetaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecetaScreen(
    recetaId: Int,
    viewModel: RecetaViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    val receta = (estado as? RecetaUiState.Exito)
        ?.recetas?.find { it.id == recetaId }

    var editando by rememberSaveable(recetaId) {
        mutableStateOf(false)
    }

    var titulo by rememberSaveable(recetaId) {
        mutableStateOf("")
    }

    var descripcion by rememberSaveable(recetaId) {
        mutableStateOf("")
    }

    var categoria by rememberSaveable(recetaId) {
        mutableStateOf("")
    }

    var tiempoPreparacion by rememberSaveable(recetaId) {
        mutableStateOf("")
    }

    var ingredientes by rememberSaveable(recetaId) {
        mutableStateOf("")
    }

    var preparacion by rememberSaveable(recetaId) {
        mutableStateOf("")
    }

    var mensajeValidacion by remember {
        mutableStateOf("")
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    var mostrarConfirmacionEliminar by remember {
        mutableStateOf(false)
    }

    // Cargar los datos originales en los campos
    LaunchedEffect(receta) {
        if (receta != null) {
            titulo = receta.titulo
            descripcion = receta.descripcion
            categoria = receta.categoria
            tiempoPreparacion = receta.tiempoPreparacion.toString()
            ingredientes = receta.ingredientes
            preparacion = receta.preparacion
        }
    }

    // Mostrar el resultado de crear, editar o eliminar
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje!!)

            if (mensaje == "Receta actualizada") {
                editando = false
            }

            viewModel.consumirMensaje()
        }
    }
    if (mostrarConfirmacionEliminar && receta != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacionEliminar = false
            },
            title = {
                Text("Eliminar receta")
            },
            text = {
                Text("¿Estás seguro de que deseas eliminar '${receta.titulo}'?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarConfirmacionEliminar = false
                        viewModel.eliminarReceta(receta.id)
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mostrarConfirmacionEliminar = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text("Detalle de la receta")
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a recetas"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (estado is RecetaUiState.Cargando) {

                CircularProgressIndicator()
                Text("Cargando receta…")

            } else if (estado is RecetaUiState.Error) {

                Text(
                    text = (estado as RecetaUiState.Error).mensaje,
                    style = MaterialTheme.typography.bodyLarge
                )

                Button(
                    onClick = { viewModel.cargarRecetas() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reintentar")
                }

            } else if (receta != null) {

                if (editando) {

                    Text(
                        text = "Editar receta",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tiempoPreparacion,
                        onValueChange = { tiempoPreparacion = it },
                        label = { Text("Tiempo de preparación (minutos)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ingredientes,
                        onValueChange = { ingredientes = it },
                        label = { Text("Ingredientes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    OutlinedTextField(
                        value = preparacion,
                        onValueChange = { preparacion = it },
                        label = { Text("Preparación") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )

                    if (mensajeValidacion.isNotEmpty()) {
                        Text(
                            text = mensajeValidacion,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            val minutos = tiempoPreparacion.toIntOrNull()

                            if (
                                titulo.isBlank() ||
                                descripcion.isBlank() ||
                                categoria.isBlank() ||
                                ingredientes.isBlank() ||
                                preparacion.isBlank() ||
                                minutos == null ||
                                minutos <= 0
                            ) {
                                mensajeValidacion =
                                    "Completa todos los campos y verifica el tiempo."
                            } else {
                                mensajeValidacion = ""

                                viewModel.actualizarReceta(
                                    Receta(
                                        id = receta.id,
                                        titulo = titulo.trim(),
                                        descripcion = descripcion.trim(),
                                        categoria = categoria.trim(),
                                        tiempoPreparacion = minutos,
                                        ingredientes = ingredientes.trim(),
                                        preparacion = preparacion.trim()
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar cambios")
                    }

                    Button(
                        onClick = {
                            titulo = receta.titulo
                            descripcion = receta.descripcion
                            categoria = receta.categoria
                            tiempoPreparacion = receta.tiempoPreparacion.toString()
                            ingredientes = receta.ingredientes
                            preparacion = receta.preparacion
                            mensajeValidacion = ""
                            editando = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar edición")
                    }

                } else {

                    Text(
                        text = receta.titulo,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "Categoría: ${receta.categoria}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = receta.descripcion,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Tiempo de preparación: ${receta.tiempoPreparacion} minutos",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Ingredientes",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = receta.ingredientes,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Preparación",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = receta.preparacion,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = {
                            editando = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Editar receta")
                    }
                    Button(
                        onClick = {
                            mostrarConfirmacionEliminar = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar receta")
                    }
                }

            } else {

                Text("No se encontró la receta.")
            }

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver a recetas")
            }
        }
    }
}

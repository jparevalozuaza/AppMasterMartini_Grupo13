package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mastermartini.model.Post
import cl.duoc.mastermartini.ui.state.PostUiState
import cl.duoc.mastermartini.viewmodel.PostViewModel

// ─────────────────────────────────────────────────────────────
// GUÍA 14 · PANTALLA "LISTADO DE POSTS" CON CRUD
// GET al abrir · POST con el botón + · PUT con el lápiz · DELETE con el basurero.
// La pantalla solo muestra estado y avisa eventos al ViewModel.
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    onIrAMartini: () -> Unit,                     // acción del botón para ir al Login de Martini App
    viewModel: PostViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    // Estado propio de la pantalla (no es dato de negocio): qué diálogo está abierto
    var mostrarFormulario by remember { mutableStateOf(false) }
    var postEnEdicion by remember { mutableStateOf<Post?>(null) }   // null = creando uno nuevo
    var postAEliminar by remember { mutableStateOf<Post?>(null) }

    // Muestra cada mensaje del ViewModel en un Snackbar y luego lo "consume"
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.consumirMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de Posts") },
                actions = { TextButton(onClick = onIrAMartini) { Text("Ir a Martini App") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                postEnEdicion = null
                mostrarFormulario = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Crear post")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val e = estado) {
                is PostUiState.Cargando -> CircularProgressIndicator()

                is PostUiState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(e.mensaje, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                    Button(onClick = { viewModel.cargarPosts() }) { Text("Reintentar") }
                }

                is PostUiState.Exito -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(e.posts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            onEditar = {
                                postEnEdicion = post
                                mostrarFormulario = true
                            },
                            onEliminar = { postAEliminar = post }
                        )
                    }
                }
            }
        }
    }

    // Diálogo para crear (POST) o editar (PUT)
    if (mostrarFormulario) {
        FormularioPost(
            postInicial = postEnEdicion,
            onCancelar = { mostrarFormulario = false },
            onGuardar = { titulo, cuerpo ->
                val original = postEnEdicion
                if (original == null) viewModel.crearPost(titulo, cuerpo)
                else viewModel.actualizarPost(original, titulo, cuerpo)
                mostrarFormulario = false
            }
        )
    }

    // Confirmación antes de eliminar (DELETE)
    postAEliminar?.let { post ->
        AlertDialog(
            onDismissRequest = { postAEliminar = null },
            title = { Text("Eliminar post") },
            text = { Text("¿Eliminar el post #${post.id}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarPost(post)
                    postAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { postAEliminar = null }) { Text("Cancelar") } }
        )
    }
}

// Tarjeta de un post con botones de editar y eliminar
@Composable
private fun PostCard(post: Post, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Post #${post.id}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEditar) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar post ${post.id}")
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar post ${post.id}")
                }
            }
            Text(post.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(post.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Formulario reutilizable: vacío para crear, con datos para editar
@Composable
private fun FormularioPost(
    postInicial: Post?,
    onCancelar: () -> Unit,
    onGuardar: (titulo: String, cuerpo: String) -> Unit
) {
    var titulo by remember { mutableStateOf(postInicial?.title ?: "") }
    var cuerpo by remember { mutableStateOf(postInicial?.body ?: "") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (postInicial == null) "Nuevo post" else "Editar post #${postInicial.id}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = cuerpo,
                    onValueChange = { cuerpo = it },
                    label = { Text("Contenido") },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onGuardar(titulo, cuerpo) },
                enabled = titulo.isNotBlank() && cuerpo.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

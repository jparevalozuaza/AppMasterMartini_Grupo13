package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mastermartini.model.Post
import cl.duoc.mastermartini.ui.state.PostUiState
import cl.duoc.mastermartini.viewmodel.PostViewModel

// ─────────────────────────────────────────────────────────────
// GUÍA 14 · PANTALLA "LISTADO DE POSTS"
// Solo muestra el estado que entrega el ViewModel: no conoce Retrofit ni la API.
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    onIrAMartini: () -> Unit,                     // acción del botón para ir al Login de Martini App
    viewModel: PostViewModel = viewModel()
) {
    // collectAsState: cada vez que cambia el StateFlow, la pantalla se vuelve a dibujar
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de Posts") },
                actions = { TextButton(onClick = onIrAMartini) { Text("Ir a Martini App") } }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // "when" sobre una sealed interface: hay que cubrir los tres estados
            when (val e = estado) {
                is PostUiState.Cargando -> CircularProgressIndicator()

                is PostUiState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(e.mensaje, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                    Button(onClick = { viewModel.cargarPosts() }) { Text("Reintentar") }
                }

                is PostUiState.Exito -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "key" ayuda a Compose a identificar cada tarjeta al agregar o quitar posts
                    items(e.posts, key = { it.id }) { post -> PostCard(post) }
                }
            }
        }
    }
}

// Tarjeta de un post: id, título y contenido
@Composable
private fun PostCard(post: Post) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Post #${post.id}", style = MaterialTheme.typography.labelMedium)
            Text(post.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(post.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.ui.state.RecetaUiState
import cl.duoc.mastermartini.viewmodel.RecetaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecetaScreen(
    recetaId: Int,
    viewModel: RecetaViewModel,
    onVolver: () -> Unit,
    onEditar: (Int) -> Unit,        // abre el formulario con esta receta
    onEliminada: () -> Unit,        // vuelve a la lista después de eliminar
    esAdministrador: Boolean        // true: muestra Editar y Eliminar
) {
    // La receta se busca en la lista que entrega el ViewModel
    val estado by viewModel.estado.collectAsState()
    val receta = (estado as? RecetaUiState.Exito)?.recetas?.find { it.id == recetaId }

    // true mientras se muestra el diálogo "¿Eliminar receta?"
    var confirmarEliminacion by remember { mutableStateOf(false) }

    Scaffold(
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
                Text(
                    text = "Cargando receta…",
                    style = MaterialTheme.typography.bodyLarge
                )

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

                // Acciones del CRUD sobre esta receta: solo para administradores
                if (esAdministrador) {
                    OutlinedButton(
                        onClick = { onEditar(receta.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Editar receta")
                    }

                    OutlinedButton(
                        onClick = { confirmarEliminacion = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar receta")
                    }
                }

            } else {

                Text(
                    text = "No se encontró la receta.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver a recetas")
            }
        }
    }

    // Diálogo de confirmación: eliminar no se puede deshacer, por eso se pregunta antes
    if (confirmarEliminacion && receta != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminacion = false },
            title = { Text("¿Eliminar receta?") },
            text = { Text("Se eliminará \"${receta.titulo}\". Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminacion = false
                    viewModel.eliminarReceta(receta.id)   // DELETE; el mensaje se verá en la lista
                    onEliminada()
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

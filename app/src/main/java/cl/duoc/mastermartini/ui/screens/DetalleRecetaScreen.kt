
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.viewmodel.RecetaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecetaScreen(
    recetaId: Int,
    viewModel: RecetaViewModel,
    onVolver: () -> Unit
) {
    val recetas by viewModel.recetas.collectAsState()
    val receta = recetas.find { it.id == recetaId }

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

            if (receta != null) {

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
}

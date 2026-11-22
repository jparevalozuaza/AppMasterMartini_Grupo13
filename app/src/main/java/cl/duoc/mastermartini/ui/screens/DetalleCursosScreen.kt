package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.model.Curso
import cl.duoc.mastermartini.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCursoScreen(
    curso: Curso,
    viewModel: MainViewModel,
    onAbrirLeccion: (Int) -> Unit,
    onVolver: () -> Unit
) {
    val progreso = viewModel.progreso.value
    val leccionesCompletadas = curso.lecciones.count { it.id in progreso }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(curso.titulo) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a cursos"
                        )
                    }
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
                    text = curso.categoria,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = curso.titulo,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = curso.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Contenido del curso",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 20.dp)
                )

                Text(
                    text = "$leccionesCompletadas de ${curso.lecciones.size} lecciones completadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(curso.lecciones) { leccion ->
                val completada = leccion.id in progreso

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAbrirLeccion(leccion.id)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Lección ${leccion.id}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = leccion.titulo,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = leccion.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = if (completada) {
                                "✓ Completada"
                            } else {
                                "Pendiente"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (completada) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        Text(
                            text = "Abrir lección →",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

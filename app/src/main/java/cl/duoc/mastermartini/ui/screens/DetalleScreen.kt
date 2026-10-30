package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.model.Contenido
import cl.duoc.mastermartini.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    contenido: Contenido?,
    viewModel: MainViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del contenido") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (contenido != null) {
                Text(
                    text = contenido.titulo,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Categoría: ${contenido.categoria}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = contenido.descripcion,
                    style = MaterialTheme.typography.bodyLarge
                )

                val estaCompletado = viewModel.estaCompletado(contenido.id)

                val colorEstado by animateColorAsState(
                    targetValue = if (estaCompletado) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    animationSpec = tween(durationMillis = 400),
                    label = "Color del estado"
                )

                Text(
                    text = if (estaCompletado) {
                        "Estado: Completado ✓"
                    } else {
                        "Estado: Pendiente"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = colorEstado
                )

                Button(
                    onClick = {
                        viewModel.alternarProgreso(contenido.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = estaCompletado,
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(250)
                            ) togetherWith fadeOut(
                                animationSpec = tween(150)
                            )
                        },
                        label = "Cambio del texto de progreso"
                    ) { completado ->
                        Text(
                            text = if (completado) {
                                "Marcar como pendiente"
                            } else {
                                "Marcar como completado"
                            }
                        )
                    }
                }

                val esFavorito =
                    viewModel.favoritos.value.contains(contenido.id)

                OutlinedButton(
                    onClick = {
                        viewModel.alternarFavorito(contenido.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (esFavorito) {
                            "★ Quitar de favoritos"
                        } else {
                            "☆ Agregar a favoritos"
                        }
                    )
                }
            } else {
                Text("No se encontró el contenido solicitado.")
            }
        }
    }
}

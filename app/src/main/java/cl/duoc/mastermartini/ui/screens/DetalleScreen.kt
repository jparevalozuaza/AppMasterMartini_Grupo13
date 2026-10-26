package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.model.Contenido
import cl.duoc.mastermartini.viewmodel.MainViewModel
import androidx.compose.material3.OutlinedButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    contenido: Contenido?,
    viewModel: MainViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del contenido") }
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

                val esFavorito = viewModel.favoritos.value.contains(contenido.id)

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

            Button(
                onClick = { viewModel.navigateBack() }
            ) {
                Text("Volver")
            }
        }
    }
}

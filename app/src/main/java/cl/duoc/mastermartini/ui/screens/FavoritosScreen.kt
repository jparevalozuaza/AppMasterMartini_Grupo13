package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.repository.ContenidoRepository
import cl.duoc.mastermartini.viewmodel.MainViewModel
import androidx.compose.material3.OutlinedButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(viewModel: MainViewModel) {

    val idsFavoritos = viewModel.favoritos.value

    val contenidosFavoritos = ContenidoRepository.contenidos.filter {
        idsFavoritos.contains(it.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis favoritos") }
            )
        }
    ) { innerPadding ->

        if (contenidosFavoritos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Todavía no tienes favoritos",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Abre un contenido y pulsa «Agregar a favoritos» para guardarlo aquí.",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(contenidosFavoritos) { contenido ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.navigateToRoute(
                                    Screen.Detalle.crearRuta(contenido.id)
                                )
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = contenido.titulo,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = contenido.categoria,
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                text = contenido.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "★ Favorito · Ver detalle",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )

                            OutlinedButton(
                                onClick = {
                                    viewModel.alternarFavorito(contenido.id)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Quitar de favoritos")
                            }
                        }
                    }
                }
            }
        }
    }
}

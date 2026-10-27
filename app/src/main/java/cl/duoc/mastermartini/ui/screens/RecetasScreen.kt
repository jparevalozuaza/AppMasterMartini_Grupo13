package cl.duoc.mastermartini.ui.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.viewmodel.RecetaViewModel
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import cl.duoc.mastermartini.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetasScreen(viewModel: RecetaViewModel, navController: NavController) {

    val recetas by viewModel.recetas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Recetas")
                }
            )
        }
    ) { innerPadding ->

        if (recetas.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Cargando recetas...",
                    style = MaterialTheme.typography.bodyLarge
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
                items(recetas) { receta ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable{navController.navigate("detalle/${receta.id}")
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = receta.categoria,
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                text = receta.titulo,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = receta.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "Preparación: ${receta.tiempoPreparacion} minutos",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

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
import cl.duoc.mastermartini.repository.CursoRepository
import cl.duoc.mastermartini.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursosScreen(viewModel: MainViewModel) {

    val cursos = CursoRepository.cursos

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Cursos")
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
                    text = "Nuestros cursos",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Explora nuestros cursos y desarrolla nuevas habilidades gastronómicas.",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            items(cursos) { curso ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.navigateToRoute(
                                Screen.DetalleCurso.crearRuta(curso.id)
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = curso.categoria,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = curso.titulo,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = curso.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "${curso.lecciones.size} lecciones",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Text(
                            text = "Ver curso →",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

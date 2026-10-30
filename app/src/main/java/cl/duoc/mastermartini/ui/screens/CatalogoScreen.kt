package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.repository.ContenidoRepository
import cl.duoc.mastermartini.viewmodel.MainViewModel

@Composable
fun CatalogoScreen(viewModel: MainViewModel) {

    val categorias = listOf(
        "Chocolatería",
        "Pastelería",
        "Panadería",
        "Heladería"
    )

    var categoriaSeleccionada by remember {
        mutableStateOf<String?>(null)
    }

    var textoBusqueda by remember {
        mutableStateOf("")
    }

    var soloFavoritos by remember {
        mutableStateOf(false)
    }

    val contenidos = ContenidoRepository.contenidos.filter { contenido ->
        val coincideCategoria =
            categoriaSeleccionada == null ||
                    contenido.categoria == categoriaSeleccionada

        val coincideBusqueda =
            contenido.titulo.contains(textoBusqueda, ignoreCase = true) ||
                    contenido.descripcion.contains(textoBusqueda, ignoreCase = true)

        val coincideFavoritos =
            !soloFavoritos ||
                    viewModel.favoritos.value.contains(contenido.id)

        coincideCategoria && coincideBusqueda && coincideFavoritos
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Catálogo",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            label = { Text("Buscar contenido") },
            placeholder = { Text("Ej. chocolate, masas, heladería") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedButton(
            onClick = {
                soloFavoritos = !soloFavoritos
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (soloFavoritos) {
                    "Mostrar todos los contenidos"
                } else {
                    "Mostrar solo favoritos"
                }
            )
        }

        Text(
            text = "Elige una categoría",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedButton(
            onClick = {
                categoriaSeleccionada = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Todas las categorías")
        }

        categorias.forEach { categoria ->
            if (categoriaSeleccionada == categoria) {
                Button(
                    onClick = {
                        categoriaSeleccionada = categoria
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = categoria,
                        fontSize = 18.sp
                    )
                }
            } else {
                OutlinedButton(
                    onClick = {
                        categoriaSeleccionada = categoria
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = categoria,
                        fontSize = 18.sp
                    )
                }
            }
        }

        if (categoriaSeleccionada == null) {
            Text(
                text = "Explora todas las categorías o selecciona una para filtrar.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = if (contenidos.isEmpty()) {
                "No se encontraron contenidos"
            } else {
                "Resultados: ${contenidos.size}"
            },
            style = MaterialTheme.typography.titleLarge
        )

        contenidos.forEach { contenido ->
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
                    verticalArrangement = Arrangement.spacedBy(6.dp)
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
                }
            }
        }
    }
}

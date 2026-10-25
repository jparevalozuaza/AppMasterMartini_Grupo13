package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme


@Composable
fun HomeScreenCompacta() {

    var busqueda by remember { mutableStateOf("") }

    val contenidos = listOf(
        "Recetas de chocolatería",
        "Pastelería de vitrina",
        "Técnicas de panadería",
        "Introducción a la heladería"
    )

    val contenidosFiltrados = contenidos.filter {
        it.contains(busqueda, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "¡Bienvenido a Escuela Martini!",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Aprende, crea y lleva tus conocimientos " +
                            "gastronómicos al siguiente nivel."
                )
            }
        }

        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar contenido") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            Text(
                text = "Explora nuestras especialidades",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("• Chocolatería")
                Text("• Pastelería")
                Text("• Panadería")
                Text("• Heladería")
            }
        }

        item {
            Text(
                text = "Contenido para aprender",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (contenidosFiltrados.isEmpty()) {
            item {
                Text("No se encontraron contenidos.")
            }
        } else {
            items(contenidosFiltrados) { contenido ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = contenido,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Descubre técnicas y conocimientos " +
                                    "para desarrollar tus habilidades."
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Compact", widthDp = 360, heightDp = 800)
@Composable
fun PreviewCompact() {
    HomeScreenCompacta()
}

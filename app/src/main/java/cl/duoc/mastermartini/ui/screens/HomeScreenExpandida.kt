package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.R
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.viewmodel.MainViewModel

@Composable
fun HomeScreenExpandida(viewModel: MainViewModel? = null) {

    var nombre by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                if (nombre.isBlank()) "¡Bienvenido!" else "¡Bienvenido, $nombre!",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Explora nuestras opciones:",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = {
                    viewModel?.navigateToRoute(Screen.Catalogo.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir al Catálogo")
            }

            Button(
                onClick = {
                    viewModel?.navigateToRoute(Screen.Recetas.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Recetas")
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Escuela Martini",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

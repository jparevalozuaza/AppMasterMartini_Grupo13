package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun HomeScreenExpandida() {
    var nombre by remember { mutableStateOf("") }
    Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                if (nombre.isBlank()) "¡Bienvenido!" else "¡Bienvenido, $nombre!",
                style = MaterialTheme.typography.headlineMedium
            )
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            Button(onClick = {}) { Text("Presiona") }
        }
        Spacer(modifier = Modifier.width(24.dp))
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }
}

@Preview(name = "Expanded", widthDp = 1000, heightDp = 800)
@Composable
fun PreviewExpanded() {
    HomeScreenExpandida()
}
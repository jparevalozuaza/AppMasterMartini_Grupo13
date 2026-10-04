package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenMediana() {
    var nombre by remember { mutableStateOf("") }
    Scaffold(topBar = { TopAppBar(title = { Text("Mi App Kotlin") }) }) { innerPadding ->
        Row(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(if (nombre.isBlank()) "¡Bienvenido!" else "¡Bienvenido, $nombre!")
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            }
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "logo", modifier = Modifier.weight(1f).height(150.dp))
        }
    }
}

@Preview(name = "Medium", widthDp = 700, heightDp = 800)
@Composable
fun PreviewMedium(){
    HomeScreenMediana()
}
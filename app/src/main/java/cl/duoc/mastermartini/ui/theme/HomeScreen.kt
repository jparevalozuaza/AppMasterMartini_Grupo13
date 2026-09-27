package cl.duoc.mastermartini.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.R

// Pantalla principal (Home) de la app
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(){
    //Se creea una variable y se le asigna con by el string que escribe el usuario
    var nombre by remember { mutableStateOf("") }
    Scaffold(
        // Barra superior con el título de la app
        topBar = {
            TopAppBar(
                title = { Text("Mi App Kotlin") },
                colors = TopAppBarDefaults.topAppBarColors(
                    //uso de colores desde MaterialTheme
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
// Contenedor principal de la pantalla
        Column(
            modifier = Modifier
                // respeta el espacio que deja la TopAppBar
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            //USO  de elemento alinear contenido
            horizontalAlignment = Alignment.CenterHorizontally,
            // Uso de verticalArrangement espaciado uniforme entre elementos
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            //Nuevos Elementos Vizuales y Saludo Donde se utiliza la variable
            Text(
                text = if (nombre.isBlank()) "Hola!!!!!!" else "Holasssss $nombre!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            //Campo de texto donde escribe el usuario
            OutlinedTextField(
                value = nombre,
                onValueChange = {nombre=it},
                label = {Text("INGRESA TU NOMBRE")},
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = "¡Bienvenido!")
            Button(onClick = {/*accion futura*/}) {
                Text("Presioname")
            }
            // Imagen del logo de la app
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo App",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}
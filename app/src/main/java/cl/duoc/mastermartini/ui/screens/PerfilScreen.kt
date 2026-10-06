package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.viewmodel.MainViewModel

// Pantalla de Perfil: por ahora solo prueba la navegación.
// Aquí irán la foto (cámara) y los datos del usuario más adelante.
@Composable
fun PerfilScreen(viewModel: MainViewModel) {
    // Scaffold sin barras: igual entrega innerPadding para no quedar bajo la barra de estado
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,           // centrado vertical
            horizontalAlignment = Alignment.CenterHorizontally  // centrado horizontal
        ) {
            Text("Mi perfil", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            // Ir a Inicio (usa navigateTo)
            Button(
                onClick = { viewModel.navigateTo(Screen.Home) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) { Text("Ir al inicio", fontSize = 18.sp) }

            Spacer(modifier = Modifier.height(16.dp))

            // Volver a la pantalla anterior (usa navigateBack → PopBackStack)
            OutlinedButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) { Text("Volver atrás", fontSize = 18.sp) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    PerfilScreen(viewModel = MainViewModel())
}
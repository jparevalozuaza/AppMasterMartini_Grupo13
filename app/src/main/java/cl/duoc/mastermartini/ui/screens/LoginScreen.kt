package cl.duoc.mastermartini.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.R
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.ui.components.MartiniTextField
import cl.duoc.mastermartini.ui.components.PrimaryButton
import cl.duoc.mastermartini.ui.components.SecondaryButton
import cl.duoc.mastermartini.viewmodel.AuthViewModel
import cl.duoc.mastermartini.viewmodel.MainViewModel
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
// PANTALLA 1: BIENVENIDA / LOGIN (Integrante 1) — guía sección 6.1
// ─────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel
) {
    // La pantalla "observa" el estado: cada cambio en el ViewModel la redibuja
    val estado by authViewModel.login.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())   // permite desplazar si la pantalla es pequeña
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo y propuesta de valor (reemplazan a una pantalla Splash separada)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Martini App",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(28.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Martini App",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Conocimiento que impulsa tu negocio",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Ingresa para continuar aprendiendo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo correo
            MartiniTextField(
                valor = estado.email,
                onValorChange = authViewModel::onLoginEmailChange,
                etiqueta = "Correo electrónico",
                error = estado.emailError,
                icono = Icons.Filled.Email,
                tipoTeclado = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo contraseña (se muestra con puntos)
            MartiniTextField(
                valor = estado.clave,
                onValorChange = authViewModel::onLoginClaveChange,
                etiqueta = "Contraseña",
                error = estado.claveError,
                icono = Icons.Filled.Lock,
                esClave = true,
                imeAction = ImeAction.Done
            )

            // Recuperar contraseña: solo informativo en el MVP
            TextButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Demo: para recuperar tu contraseña contacta a Master Martini.")
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("¿Olvidaste tu contraseña?", fontWeight = FontWeight.Bold)
            }

            // ANIMACIÓN: tarjeta de error general (correo o contraseña incorrectos)
            AnimatedVisibility(visible = estado.loginError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = estado.loginError ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Botón principal: se deshabilita si falta información
            PrimaryButton(
                texto = "Ingresar",
                onClick = {
                    authViewModel.iniciarSesion(
                        onExito = { mainViewModel.navegarLimpiando(Screen.Home, Screen.Login) }
                    )
                },
                habilitado = estado.puedeIngresar,
                cargando = estado.cargando
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "¿Primera vez en Martini App?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            SecondaryButton(
                texto = "Crear cuenta",
                onClick = { mainViewModel.navigateToRoute(Screen.Registro.route) }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Cuenta demo: demo@martini.cl · martini123",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Datos ficticios para demostración académica",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

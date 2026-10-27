package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.model.OpcionesRegistro
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.ui.components.BotonUbicacion
import cl.duoc.mastermartini.ui.components.ChipsSeleccion
import cl.duoc.mastermartini.ui.components.MartiniTextField
import cl.duoc.mastermartini.ui.components.PrimaryButton
import cl.duoc.mastermartini.ui.components.TituloSeccion
import cl.duoc.mastermartini.viewmodel.AuthViewModel
import cl.duoc.mastermartini.viewmodel.MainViewModel

// ─────────────────────────────────────────────────────────────
// PANTALLA 2: REGISTRO (Integrante 1) — guía sección 6.2
// Un único formulario desplazable con 3 secciones: Datos, Negocio, Intereses.
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel
) {
    val estado by authViewModel.registro.collectAsState()
    val errores = estado.errores
    val snackbarHostState = remember { SnackbarHostState() }

    // Cuando el ViewModel deja un mensaje, se muestra en un Snackbar y luego se "consume"
    LaunchedEffect(estado.mensaje) {
        val mensaje = estado.mensaje
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            authViewModel.consumirMensajeRegistro()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Crear cuenta", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                        Text(
                            "Una sola pantalla · campos esenciales",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---------- SECCIÓN 1: DATOS PERSONALES ----------
            TituloSeccion("1. Tus datos")
            MartiniTextField(
                valor = estado.nombre,
                onValorChange = authViewModel::onNombreChange,
                etiqueta = "Nombre completo",
                error = errores.nombre,
                icono = Icons.Filled.Person
            )
            MartiniTextField(
                valor = estado.email,
                onValorChange = authViewModel::onEmailChange,
                etiqueta = "Correo electrónico",
                error = errores.email,
                icono = Icons.Filled.Email,
                tipoTeclado = KeyboardType.Email
            )
            MartiniTextField(
                valor = estado.telefono,
                onValorChange = authViewModel::onTelefonoChange,
                etiqueta = "Teléfono (+56 9 ...)",
                error = errores.telefono,
                icono = Icons.Filled.Phone,
                tipoTeclado = KeyboardType.Phone
            )
            MartiniTextField(
                valor = estado.clave,
                onValorChange = authViewModel::onClaveChange,
                etiqueta = "Contraseña (mínimo 6 caracteres)",
                error = errores.clave,
                icono = Icons.Filled.Lock,
                esClave = true
            )

            // ---------- SECCIÓN 2: NEGOCIO ----------
            TituloSeccion("2. Tu negocio")
            MartiniTextField(
                valor = estado.rut,
                onValorChange = authViewModel::onRutChange,
                etiqueta = "RUT de empresa (opcional)",
                error = errores.rut,
                icono = Icons.Filled.AccountBox
            )
            Text("Tipo de negocio", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            ChipsSeleccion(
                opciones = OpcionesRegistro.tiposNegocio,
                seleccionados = setOf(estado.tipoNegocio),       // selección única
                onClick = authViewModel::onTipoNegocioChange,
                error = errores.tipoNegocio
            )
            Text("Tamaño", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            ChipsSeleccion(
                opciones = OpcionesRegistro.tamanos,
                seleccionados = setOf(estado.tamanoNegocio),     // selección única
                onClick = authViewModel::onTamanoNegocioChange,
                error = errores.tamanoNegocio
            )
            MartiniTextField(
                valor = estado.direccion,
                onValorChange = authViewModel::onDireccionChange,
                etiqueta = "Dirección (opcional)",
                icono = Icons.Filled.Place,
                imeAction = ImeAction.Done
            )
            // RECURSO NATIVO: ubicación. Si se rechaza el permiso, el campo manual sigue disponible.
            BotonUbicacion(
                onDireccionObtenida = authViewModel::onDireccionChange,
                onMensaje = authViewModel::mostrarMensajeRegistro
            )

            // ---------- SECCIÓN 3: INTERESES ----------
            TituloSeccion("3. Tus intereses")
            ChipsSeleccion(
                opciones = OpcionesRegistro.intereses,
                seleccionados = estado.intereses,               // selección múltiple
                onClick = authViewModel::onInteresClick,
                error = errores.intereses
            )

            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButton(
                texto = "Crear cuenta",
                onClick = {
                    authViewModel.registrar(
                        onExito = { mainViewModel.navegarLimpiando(Screen.Home, Screen.Login) }
                    )
                },
                cargando = estado.guardando
            )
            Text(
                text = "Al continuar aceptas usar datos ficticios de prueba.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }
    }
}

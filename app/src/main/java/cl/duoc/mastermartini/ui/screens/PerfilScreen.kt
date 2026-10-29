package cl.duoc.mastermartini.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.navigation.Screen
import cl.duoc.mastermartini.ui.components.AvatarPerfil
import cl.duoc.mastermartini.ui.components.MartiniTextField
import cl.duoc.mastermartini.ui.components.PrimaryButton
import cl.duoc.mastermartini.ui.components.SecondaryButton
import cl.duoc.mastermartini.ui.components.TituloSeccion
import cl.duoc.mastermartini.viewmodel.MainViewModel
import cl.duoc.mastermartini.viewmodel.PerfilViewModel

// ─────────────────────────────────────────────────────────────
// PANTALLA 8: PERFIL / CONFIGURACIÓN (Integrante 1) — guía sección 6.8
// Edición de datos, foto desde la galería, accesibilidad y cierre de sesión.
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    mainViewModel: MainViewModel,
    perfilViewModel: PerfilViewModel
) {
    val estado by perfilViewModel.estado.collectAsState()
    val textoGrande by perfilViewModel.textoGrande.collectAsState()
    val reducirMovimiento by perfilViewModel.reducirMovimiento.collectAsState()
    val subtitulos by perfilViewModel.subtitulos.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Al entrar a la pantalla se carga el perfil del usuario con sesión iniciada
    LaunchedEffect(Unit) { perfilViewModel.cargarPerfil() }

    // Mensajes del ViewModel → Snackbar
    LaunchedEffect(estado.mensaje) {
        val mensaje = estado.mensaje
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            perfilViewModel.consumirMensaje()
        }
    }

    // ---------- RECURSO NATIVO: GALERÍA ----------
    // GetContent("image/*") abre el selector de imágenes del sistema.
    // No necesita permisos: Android solo entrega acceso a la imagen que el usuario elige.
    // Devuelve la Uri (dirección) de la imagen, o null si el usuario cancela.
    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            perfilViewModel.guardarFotoDesdeGaleria(uri)
        } else {
            perfilViewModel.mostrarMensaje("No elegiste ninguna imagen.")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary) },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val usuario = estado.usuario

        when {
            // Estado de carga
            estado.cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Sin sesión: se ofrece volver al Login
            usuario == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No hay una sesión iniciada.", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        texto = "Iniciar sesión",
                        onClick = { mainViewModel.navegarLimpiando(Screen.Login, Screen.Home) }
                    )
                }
            }

            // Perfil cargado
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ---------- TARJETA DE PERFIL ----------
                    TarjetaBlanca {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarPerfil(fotoPath = usuario.fotoPath, nombre = usuario.nombre)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = usuario.nombre,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "${usuario.tipoNegocio} · ${usuario.tamanoNegocio}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = usuario.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { perfilViewModel.iniciarEdicion() },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                            ) {
                                Text("Editar datos", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { lanzadorGaleria.launch("image/*") },
                                enabled = !estado.guardandoFoto,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                            ) {
                                if (estado.guardandoFoto) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Cambiar foto", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // ---------- FORMULARIO DE EDICIÓN (aparece con animación) ----------
                    AnimatedVisibility(visible = estado.editando) {
                        TarjetaBlanca {
                            Text("Editar datos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            MartiniTextField(
                                valor = estado.nombre,
                                onValorChange = perfilViewModel::onNombreChange,
                                etiqueta = "Nombre completo",
                                error = estado.nombreError,
                                icono = Icons.Filled.Person
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            MartiniTextField(
                                valor = estado.telefono,
                                onValorChange = perfilViewModel::onTelefonoChange,
                                etiqueta = "Teléfono",
                                error = estado.telefonoError,
                                icono = Icons.Filled.Phone,
                                tipoTeclado = KeyboardType.Phone
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            MartiniTextField(
                                valor = estado.direccion,
                                onValorChange = perfilViewModel::onDireccionChange,
                                etiqueta = "Dirección",
                                icono = Icons.Filled.Place,
                                imeAction = ImeAction.Done
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SecondaryButton(
                                    texto = "Cancelar",
                                    onClick = { perfilViewModel.cancelarEdicion() },
                                    modifier = Modifier.weight(1f)
                                )
                                PrimaryButton(
                                    texto = "Guardar",
                                    onClick = { perfilViewModel.guardarCambios() },
                                    cargando = estado.guardando,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // ---------- DATOS Y RECURSOS ----------
                    TituloSeccion("Datos del negocio")
                    TarjetaBlanca {
                        DatoPerfil(etiqueta = "Dirección", valor = usuario.direccion.ifBlank { "Sin dirección registrada" })
                        Spacer(modifier = Modifier.height(8.dp))
                        DatoPerfil(etiqueta = "Intereses", valor = usuario.intereses.replace(",", " · "))
                        Spacer(modifier = Modifier.height(8.dp))
                        DatoPerfil(etiqueta = "Teléfono", valor = usuario.telefono)
                        if (usuario.rut.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            DatoPerfil(etiqueta = "RUT empresa", valor = usuario.rut)
                        }
                    }

                    // ---------- PREFERENCIAS DE ACCESIBILIDAD (DataStore) ----------
                    TituloSeccion("Preferencias de accesibilidad")
                    FilaPreferencia(
                        titulo = "Texto grande",
                        descripcion = "Aumenta el tamaño de las letras",
                        activado = textoGrande,
                        onCambio = perfilViewModel::setTextoGrande
                    )
                    FilaPreferencia(
                        titulo = "Reducir movimiento",
                        descripcion = "Desactiva las animaciones entre pantallas",
                        activado = reducirMovimiento,
                        onCambio = perfilViewModel::setReducirMovimiento
                    )
                    FilaPreferencia(
                        titulo = "Subtítulos",
                        descripcion = "Activos en videos",
                        activado = subtitulos,
                        onCambio = perfilViewModel::setSubtitulos
                    )

                    // ---------- CERRAR SESIÓN ----------
                    OutlinedButton(
                        onClick = {
                            perfilViewModel.cerrarSesion(
                                onListo = { mainViewModel.navegarLimpiando(Screen.Login, Screen.Home) }
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "Datos ficticios para demostración académica",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}

// Tarjeta blanca con borde suave (estilo de los mockups)
@Composable
private fun TarjetaBlanca(contenido: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            contenido()
        }
    }
}

// Etiqueta pequeña + valor (ej. "Dirección" / "Av. Ejemplo 123")
@Composable
private fun DatoPerfil(etiqueta: String, valor: String) {
    Text(etiqueta, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(valor, style = MaterialTheme.typography.bodyLarge)
}

// Fila con título, descripción e interruptor. Toda la fila se puede tocar (más fácil de usar).
@Composable
private fun FilaPreferencia(
    titulo: String,
    descripcion: String,
    activado: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .toggleable(value = activado, role = Role.Switch, onValueChange = onCambio)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // onCheckedChange = null: el cambio lo maneja la fila completa (toggleable)
            Switch(checked = activado, onCheckedChange = null)
        }
    }
}

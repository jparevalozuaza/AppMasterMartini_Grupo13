package cl.duoc.mastermartini.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────
// COMPONENTES REUTILIZABLES (guía 8 pantallas, sección 4.6)
// Se usan en Login, Registro y Perfil.
// ─────────────────────────────────────────────────────────────

// MartiniTextField: campo de texto con ícono, borde rojo y mensaje de error animado.
@Composable
fun MartiniTextField(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    icono: ImageVector? = null,
    esClave: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            label = { Text(etiqueta) },
            leadingIcon = if (icono != null) {
                { Icon(imageVector = icono, contentDescription = null) }
            } else null,
            // Ícono de advertencia: el error no se comunica solo con color (accesibilidad)
            trailingIcon = if (error != null) {
                {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else null,
            isError = error != null,
            singleLine = true,
            visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (esClave) KeyboardType.Password else tipoTeclado,
                imeAction = imeAction
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
        // ANIMACIÓN: el mensaje de error aparece/desaparece suavemente
        AnimatedVisibility(visible = error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

// PrimaryButton: acción principal (verde, 56dp de alto). Muestra un indicador mientras carga.
@Composable
fun PrimaryButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    cargando: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = habilitado && !cargando,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

// SecondaryButton: acción secundaria (solo borde verde).
@Composable
fun SecondaryButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

// ChipsSeleccion: grupo de chips (FilterChip). Sirve para selección única o múltiple:
// quien lo usa decide qué hacer en onClick.
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChipsSeleccion(
    opciones: List<String>,
    seleccionados: Set<String>,
    onClick: (String) -> Unit,
    error: String? = null
) {
    Column {
        // FlowRow: pone los chips en fila y salta a la línea siguiente si no caben
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            opciones.forEach { opcion ->
                val seleccionado = opcion in seleccionados
                FilterChip(
                    selected = seleccionado,
                    onClick = { onClick(opcion) },
                    label = { Text(opcion, style = MaterialTheme.typography.bodyLarge) },
                    leadingIcon = if (seleccionado) {
                        { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.heightIn(min = 48.dp)
                )
            }
        }
        AnimatedVisibility(visible = error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

// TituloSeccion: subtítulo de sección dentro de un formulario o pantalla
@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = modifier.padding(top = 8.dp)
    )
}

// AvatarPerfil: muestra la foto guardada (circular) o, si no hay, las iniciales del nombre.
@Composable
fun AvatarPerfil(fotoPath: String?, nombre: String, tamano: Dp = 80.dp) {
    // remember(fotoPath): solo vuelve a leer el archivo cuando cambia la ruta de la foto
    val bitmap = remember(fotoPath) { fotoPath?.let { BitmapFactory.decodeFile(it) } }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(tamano)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = Modifier
                .size(tamano)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = obtenerIniciales(nombre),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

// "Camila Soto" → "CS"
fun obtenerIniciales(nombre: String): String =
    nombre.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

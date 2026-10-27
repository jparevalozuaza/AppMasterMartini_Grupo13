package cl.duoc.mastermartini.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.data.ubicacion.UbicacionHelper
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
// BOTÓN "USAR UBICACIÓN" (recurso nativo 2, guía sección 10)
// 1) Si no hay permiso, lo pide EN CONTEXTO (al tocar el botón).
// 2) Si el usuario lo rechaza, se avisa y se mantiene la dirección manual.
// 3) Si hay permiso, obtiene la dirección y la entrega con onDireccionObtenida.
// ─────────────────────────────────────────────────────────────
@Composable
fun BotonUbicacion(
    onDireccionObtenida: (String) -> Unit,
    onMensaje: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ubicacionHelper = remember { UbicacionHelper(context) }
    val scope = rememberCoroutineScope()
    var buscando by remember { mutableStateOf(false) }

    // Obtiene la dirección (tarea en segundo plano)
    fun buscarDireccion() {
        scope.launch {
            buscando = true
            val direccion = ubicacionHelper.obtenerDireccion()
            buscando = false
            if (direccion != null) {
                onDireccionObtenida(direccion)
            } else {
                onMensaje("No pudimos obtener tu ubicación. Puedes escribir la dirección manualmente.")
            }
        }
    }

    // Lanzador que muestra el diálogo de permiso del sistema
    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        if (resultados.values.any { it }) {
            buscarDireccion()
        } else {
            onMensaje("Permiso de ubicación denegado. Puedes escribir tu dirección manualmente.")
        }
    }

    OutlinedButton(
        onClick = {
            if (ubicacionHelper.tienePermiso()) {
                buscarDireccion()
            } else {
                lanzadorPermiso.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        },
        enabled = !buscando,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.heightIn(min = 48.dp)
    ) {
        if (buscando) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Buscando…")
        } else {
            Icon(Icons.Filled.LocationOn, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Usar ubicación")
        }
    }
}

package cl.duoc.mastermartini.data.ubicacion

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

// ─────────────────────────────────────────────────────────────
// ACCESO A LA UBICACIÓN DEL DISPOSITIVO (recurso nativo 2)
// Usa el "Fused Location Provider" de Google y luego Geocoder
// para transformar coordenadas en una dirección legible.
// ─────────────────────────────────────────────────────────────
class UbicacionHelper(private val context: Context) {

    // ¿El usuario ya concedió algún permiso de ubicación?
    fun tienePermiso(): Boolean {
        val fina = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val aproximada = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fina == PackageManager.PERMISSION_GRANTED || aproximada == PackageManager.PERMISSION_GRANTED
    }

    // Devuelve la dirección actual, o null si no se pudo obtener
    @SuppressLint("MissingPermission") // el permiso se revisa en la primera línea
    suspend fun obtenerDireccion(): String? {
        if (!tienePermiso()) return null

        val cliente = LocationServices.getFusedLocationProviderClient(context)

        // Convierte la "tarea" de Google en una función suspend que espera el resultado
        val ubicacion: Location? = suspendCancellableCoroutine { continuacion ->
            cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { resultado -> continuacion.resume(resultado) }
                .addOnFailureListener { continuacion.resume(null) }
        }
        if (ubicacion == null) return null

        // Geocoder puede tardar: se ejecuta fuera del hilo principal
        return withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val direcciones = Geocoder(context, Locale.getDefault())
                    .getFromLocation(ubicacion.latitude, ubicacion.longitude, 1)
                direcciones?.firstOrNull()?.getAddressLine(0) ?: formatoCoordenadas(ubicacion)
            } catch (e: Exception) {
                // Sin internet o sin servicio de direcciones: se entregan las coordenadas
                formatoCoordenadas(ubicacion)
            }
        }
    }

    private fun formatoCoordenadas(ubicacion: Location): String =
        String.format(Locale.US, "Lat %.5f, Lng %.5f", ubicacion.latitude, ubicacion.longitude)
}

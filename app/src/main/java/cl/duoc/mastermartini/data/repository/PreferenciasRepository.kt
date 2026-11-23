package cl.duoc.mastermartini.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Un único archivo DataStore para toda la app (se crea la primera vez que se usa)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferencias_martini")

// ─────────────────────────────────────────────────────────────
// PREFERENCIAS CON DATASTORE (guía sección 9.1)
// Datos pequeños tipo "clave = valor": sesión y accesibilidad.
//
// INYECCIÓN DE DEPENDENCIAS: el repositorio recibe el "almacén" DataStore.
//   - La app usa el constructor con Context (el archivo real de la app).
//   - Las pruebas unitarias entregan un DataStore en memoria, sin archivos.
// ─────────────────────────────────────────────────────────────
class PreferenciasRepository(private val almacen: DataStore<Preferences>) {

    // Constructor que usa la app: toma el archivo DataStore único de la aplicación
    constructor(context: Context) : this(context.dataStore)

    private object Claves {
        val EMAIL_SESION = stringPreferencesKey("email_sesion")
        val TEXTO_GRANDE = booleanPreferencesKey("texto_grande")
        val REDUCIR_MOVIMIENTO = booleanPreferencesKey("reducir_movimiento")
        val SUBTITULOS = booleanPreferencesKey("subtitulos")
    }

    // Flow = valor que "avisa" cada vez que cambia
    val emailSesion: Flow<String?> = almacen.data.map { it[Claves.EMAIL_SESION] }
    val textoGrande: Flow<Boolean> = almacen.data.map { it[Claves.TEXTO_GRANDE] ?: false }
    val reducirMovimiento: Flow<Boolean> = almacen.data.map { it[Claves.REDUCIR_MOVIMIENTO] ?: false }
    val subtitulos: Flow<Boolean> = almacen.data.map { it[Claves.SUBTITULOS] ?: true }

    suspend fun guardarSesion(email: String) {
        almacen.edit { it[Claves.EMAIL_SESION] = email }
    }

    suspend fun cerrarSesion() {
        almacen.edit { it.remove(Claves.EMAIL_SESION) }
    }

    suspend fun setTextoGrande(valor: Boolean) {
        almacen.edit { it[Claves.TEXTO_GRANDE] = valor }
    }

    suspend fun setReducirMovimiento(valor: Boolean) {
        almacen.edit { it[Claves.REDUCIR_MOVIMIENTO] = valor }
    }

    suspend fun setSubtitulos(valor: Boolean) {
        almacen.edit { it[Claves.SUBTITULOS] = valor }
    }
}

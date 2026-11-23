package cl.duoc.mastermartini.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

// ─────────────────────────────────────────────────────────────
// DATASTORE EN MEMORIA (solo para pruebas)
// Cumple la misma interfaz que el DataStore real (data + updateData),
// pero guarda los datos en una variable en vez de un archivo del celular.
// ─────────────────────────────────────────────────────────────
private class DataStoreEnMemoria : DataStore<Preferences> {
    private val estado = MutableStateFlow(emptyPreferences())

    override val data: Flow<Preferences> = estado

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val nuevo = transform(estado.value)
        estado.value = nuevo
        return nuevo
    }
}

// ─────────────────────────────────────────────────────────────
// PRUEBAS DEL REPOSITORIO DE PREFERENCIAS (sesión y accesibilidad)
// Cada prueba usa un DataStore en memoria nuevo: parten todas desde cero.
// ─────────────────────────────────────────────────────────────
class PreferenciasRepositoryTest : StringSpec({

    "sin datos guardados no hay sesión y se usan los valores por defecto" {
        val preferencias = PreferenciasRepository(DataStoreEnMemoria())

        runTest {
            preferencias.emailSesion.first().shouldBeNull()
            preferencias.textoGrande.first() shouldBe false
            preferencias.reducirMovimiento.first() shouldBe false
            preferencias.subtitulos.first() shouldBe true
        }
    }

    "guardarSesion guarda el correo y cerrarSesion lo borra" {
        val preferencias = PreferenciasRepository(DataStoreEnMemoria())

        runTest {
            preferencias.guardarSesion("demo@martini.cl")
            preferencias.emailSesion.first() shouldBe "demo@martini.cl"

            preferencias.cerrarSesion()
            preferencias.emailSesion.first().shouldBeNull()
        }
    }

    "las preferencias de accesibilidad se guardan y se vuelven a leer" {
        val preferencias = PreferenciasRepository(DataStoreEnMemoria())

        runTest {
            preferencias.setTextoGrande(true)
            preferencias.setReducirMovimiento(true)
            preferencias.setSubtitulos(false)

            preferencias.textoGrande.first() shouldBe true
            preferencias.reducirMovimiento.first() shouldBe true
            preferencias.subtitulos.first() shouldBe false
        }
    }
})

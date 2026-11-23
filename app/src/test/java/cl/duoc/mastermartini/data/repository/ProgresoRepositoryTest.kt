package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.dao.ProgresoDao
import cl.duoc.mastermartini.data.entity.ProgresoEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

// ─────────────────────────────────────────────────────────────
// PRUEBAS DEL REPOSITORIO DE PROGRESO (DAO de Room simulado)
// ─────────────────────────────────────────────────────────────
class ProgresoRepositoryTest : StringSpec({

    "obtenerTodos entrega el progreso que guarda Room" {
        val dao = mockk<ProgresoDao>()
        every { dao.obtenerTodos() } returns flowOf(listOf(ProgresoEntity(5, completado = true)))

        runTest {
            ProgresoRepository(dao).obtenerTodos().first() shouldBe listOf(ProgresoEntity(5, completado = true))
        }
    }

    "marcarCompletado guarda la lección como completada" {
        val dao = mockk<ProgresoDao>(relaxed = true)

        runTest { ProgresoRepository(dao).marcarCompletado(5) }

        coVerify(exactly = 1) { dao.guardar(ProgresoEntity(contenidoId = 5, completado = true)) }
    }

    "marcarPendiente borra el registro de esa lección" {
        val dao = mockk<ProgresoDao>(relaxed = true)

        runTest { ProgresoRepository(dao).marcarPendiente(5) }

        coVerify(exactly = 1) { dao.eliminar(5) }
    }
})

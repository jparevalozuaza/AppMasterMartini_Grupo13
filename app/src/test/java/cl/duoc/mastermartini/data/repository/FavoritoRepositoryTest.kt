package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.dao.FavoritoDao
import cl.duoc.mastermartini.data.entity.FavoritoEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

// ─────────────────────────────────────────────────────────────
// PRUEBAS DEL REPOSITORIO DE FAVORITOS (DAO de Room simulado)
// ─────────────────────────────────────────────────────────────
class FavoritoRepositoryTest : StringSpec({

    "obtenerTodos entrega los favoritos que guarda Room" {
        val dao = mockk<FavoritoDao>()
        every { dao.obtenerTodos() } returns flowOf(listOf(FavoritoEntity(1), FavoritoEntity(4)))

        runTest {
            FavoritoRepository(dao).obtenerTodos().first().map { it.contenidoId } shouldBe listOf(1, 4)
        }
    }

    "insertar y eliminar convierten el id del contenido en un FavoritoEntity" {
        val dao = mockk<FavoritoDao>(relaxed = true)
        val repo = FavoritoRepository(dao)

        runTest {
            repo.insertar(4)
            repo.eliminar(4)
        }

        coVerify(exactly = 1) { dao.insertar(FavoritoEntity(4)) }
        coVerify(exactly = 1) { dao.eliminar(FavoritoEntity(4)) }
    }
})

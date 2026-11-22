package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.model.Receta
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import java.io.IOException

// ─────────────────────────────────────────────────────────────
// PRUEBAS DEL REPOSITORIO SIMULADO (datos en memoria del Bloque 2)
// demoraMs = 0: no se espera la pausa que imita la red.
// ─────────────────────────────────────────────────────────────
class RecetaFakeRepositoryTest : StringSpec({

    val nueva = Receta(0, "Kuchen", "Kuchen de nuez", "Pastelería", 60, "Nueces", "Hornear")

    "parte con las 5 recetas simuladas" {
        val repo = RecetaFakeRepository(demoraMs = 0)
        runTest { repo.obtenerRecetas().size shouldBe 5 }
    }

    "crear asigna el siguiente id, como lo haría MySQL" {
        val repo = RecetaFakeRepository(demoraMs = 0)
        runTest {
            repo.crearReceta(nueva).id shouldBe 6
            repo.obtenerRecetas().size shouldBe 6
        }
    }

    "con simularError todas las operaciones fallan como si no hubiera conexión" {
        val repo = RecetaFakeRepository(demoraMs = 0).apply { simularError = true }
        runTest {
            shouldThrow<IOException> { repo.obtenerRecetas() }
        }
    }
})

package cl.duoc.mastermartini.data.repository

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// ─────────────────────────────────────────────────────────────
// PRUEBAS DEL CATÁLOGO DE CONTENIDOS (datos fijos de la app)
// Si alguien agrega un contenido con un id repetido, Favoritos y Progreso
// se confundirían: estas pruebas lo detectan.
// ─────────────────────────────────────────────────────────────
class ContenidoRepositoryTest : StringSpec({

    "hay 12 contenidos y ningún id se repite" {
        val contenidos = ContenidoRepository.contenidos

        contenidos.size shouldBe 12
        contenidos.map { it.id }.toSet().size shouldBe 12
    }

    "cada una de las 4 categorías tiene 3 contenidos" {
        ContenidoRepository.contenidos
            .groupBy { it.categoria }
            .mapValues { it.value.size } shouldBe mapOf(
                "Chocolatería" to 3, "Pastelería" to 3, "Panadería" to 3, "Heladería" to 3
            )
    }
})

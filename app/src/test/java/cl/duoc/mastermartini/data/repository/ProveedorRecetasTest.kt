package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.remote.ApiConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs

// ─────────────────────────────────────────────────────────────
// PRUEBAS DEL PROVEEDOR DE RECETAS (el "interruptor" de ApiConfig)
// ─────────────────────────────────────────────────────────────
class ProveedorRecetasTest : StringSpec({

    "entrega el repositorio que indica ApiConfig y siempre la misma instancia" {
        val primero = ProveedorRecetas.repositorio()

        if (ApiConfig.USAR_DATOS_SIMULADOS) {
            primero.shouldBeInstanceOf<RecetaFakeRepository>()
        } else {
            primero.shouldBeInstanceOf<RecetaRemoteRepository>()
        }
        // "by lazy": la lista y el detalle comparten los mismos datos
        ProveedorRecetas.repositorio() shouldBeSameInstanceAs primero
    }
})

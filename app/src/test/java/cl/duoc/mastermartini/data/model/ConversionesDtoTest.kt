package cl.duoc.mastermartini.data.model

import cl.duoc.mastermartini.model.Receta
import cl.duoc.mastermartini.model.RecetaExterna
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE LAS CONVERSIONES ENTRE EL JSON (DTO) Y LOS MODELOS DE LA APP
// ─────────────────────────────────────────────────────────────
class ConversionesDtoTest : StringSpec({

    "una receta nueva (id 0) se envía al microservicio sin id, y los campos nulos se vuelven seguros" {
        val nueva = Receta(0, "Kuchen", "Kuchen de nuez", "Pastelería", 60, "Nueces", "Hornear")
        nueva.aDto().id.shouldBeNull()

        val incompleta = RecetaDto(id = 3).aReceta()
        incompleta.titulo shouldBe ""
        incompleta.tiempoPreparacion shouldBe 0
    }

    "si DummyJSON responde con pocos campos, se completan con los datos enviados" {
        val enviada = RecetaExterna(0, "Pastel de choclo", "Chilean", "Easy", 60,
            ingredientes = listOf("Choclo"))

        val recibida = RecetaExternaDto(id = 51, name = "Pastel de choclo").aRecetaExterna(respaldo = enviada)

        recibida.id shouldBe 51
        recibida.cocina shouldBe "Chilean"
        recibida.ingredientes shouldBe listOf("Choclo")
    }
})

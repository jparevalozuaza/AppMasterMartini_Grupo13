package cl.duoc.mastermartini.data.repository

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// ─────────────────────────────────────────────────────────────
// PRUEBAS DEL CATÁLOGO DE CURSOS Y LECCIONES (datos fijos de la app)
// ─────────────────────────────────────────────────────────────
class CursoRepositoryTest : StringSpec({

    "hay 4 cursos y cada uno tiene 3 lecciones" {
        val cursos = CursoRepository.cursos

        cursos.size shouldBe 4
        cursos.all { it.lecciones.size == 3 } shouldBe true
    }

    "los ids de las lecciones no se repiten y empiezan con el número de su curso" {
        val cursos = CursoRepository.cursos
        val idsLecciones = cursos.flatMap { curso -> curso.lecciones.map { it.id } }

        idsLecciones.toSet().size shouldBe 12
        // Ejemplo: la lección 203 pertenece al curso 2
        cursos.forEach { curso ->
            curso.lecciones.forEach { leccion -> leccion.id / 100 shouldBe curso.id }
        }
    }
})

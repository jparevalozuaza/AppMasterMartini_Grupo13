package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.dao.UsuarioDao
import cl.duoc.mastermartini.data.entity.UsuarioEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

// ─────────────────────────────────────────────────────────────
// PRUEBAS DEL REPOSITORIO DE USUARIOS
// El DAO de Room se reemplaza por un mock: se prueba solo la lógica
// del repositorio (correo normalizado, contraseña con hash, cuenta demo).
// ─────────────────────────────────────────────────────────────
class UsuarioRepositoryTest : StringSpec({

    val camila = UsuarioEntity(
        id = 1, nombre = "Camila Soto", email = "demo@martini.cl", telefono = "+56 9 5555 1234",
        rut = "12.345.678-5", tipoNegocio = "Pastelería", tamanoNegocio = "Pequeño",
        direccion = "Av. Ejemplo 123, Santiago", intereses = "Panadería,Pastelería",
        claveHash = UsuarioRepository.hashClave("martini123")
    )

    "busca el correo siempre en minúsculas y sin espacios" {
        val dao = mockk<UsuarioDao>()
        coEvery { dao.buscarPorEmail("demo@martini.cl") } returns camila
        val repo = UsuarioRepository(dao)

        runTest {
            repo.buscarPorEmail("  Demo@Martini.CL ") shouldBe camila
            repo.existeEmail("demo@martini.cl") shouldBe true
        }
    }

    "validarCredenciales devuelve al usuario solo si la contraseña coincide" {
        val dao = mockk<UsuarioDao>()
        coEvery { dao.buscarPorEmail("demo@martini.cl") } returns camila
        coEvery { dao.buscarPorEmail("nadie@martini.cl") } returns null
        val repo = UsuarioRepository(dao)

        runTest {
            repo.validarCredenciales("demo@martini.cl", "martini123") shouldBe camila
            repo.validarCredenciales("demo@martini.cl", "otraClave").shouldBeNull()
            repo.validarCredenciales("nadie@martini.cl", "martini123").shouldBeNull()
        }
    }

    "el hash no guarda la contraseña en texto plano y siempre da la misma huella" {
        val huella = UsuarioRepository.hashClave("martini123")

        huella shouldNotBe "martini123"
        huella.length shouldBe 64                                  // SHA-256 = 64 caracteres hexadecimales
        UsuarioRepository.hashClave("martini123") shouldBe huella  // misma clave → misma huella
        UsuarioRepository.hashClave("martini124") shouldNotBe huella
    }

    "la cuenta demo se crea solo si todavía no existe" {
        val sinDemo = mockk<UsuarioDao>()
        coEvery { sinDemo.buscarPorEmail(any()) } returns null
        coEvery { sinDemo.insertar(any()) } returns 1L
        val conDemo = mockk<UsuarioDao>()
        coEvery { conDemo.buscarPorEmail(any()) } returns camila

        runTest {
            UsuarioRepository(sinDemo).crearUsuarioDemoSiNoExiste()
            UsuarioRepository(conDemo).crearUsuarioDemoSiNoExiste()
        }

        coVerify(exactly = 1) { sinDemo.insertar(match { it.email == UsuarioRepository.EMAIL_DEMO }) }
        coVerify(exactly = 0) { conDemo.insertar(any()) }
    }

    "registrar y actualizar envían el usuario al DAO de Room" {
        val dao = mockk<UsuarioDao>(relaxed = true)
        val repo = UsuarioRepository(dao)

        runTest {
            repo.registrar(camila)
            repo.actualizar(camila)
        }

        coVerify(exactly = 1) { dao.insertar(camila) }
        coVerify(exactly = 1) { dao.actualizar(camila) }
    }
})

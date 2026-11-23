package cl.duoc.mastermartini.viewmodel

import android.app.Application
import cl.duoc.mastermartini.data.entity.UsuarioEntity
import cl.duoc.mastermartini.data.repository.PreferenciasRepository
import cl.duoc.mastermartini.data.repository.UsuarioRepository
import cl.duoc.mastermartini.ui.state.LoginUiState
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE AuthViewModel: Login y Registro (Kotest + MockK)
// Room (UsuarioRepository) y DataStore (PreferenciasRepository) se reemplazan
// por MOCKS: no se crea ninguna base de datos ni archivo.
// relaxed = true → todo lo que no configuramos con coEvery responde un valor
// "vacío" (Unit, false, 0), así solo escribimos lo que importa en cada prueba.
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest : StringSpec({

    // Usuario de ejemplo que "devuelve" Room simulado
    val camila = UsuarioEntity(
        id = 1, nombre = "Camila Soto", email = "demo@martini.cl", telefono = "+56 9 5555 1234",
        rut = "12.345.678-5", tipoNegocio = "Pastelería", tamanoNegocio = "Pequeño",
        direccion = "Av. Ejemplo 123, Santiago", intereses = "Panadería,Pastelería",
        claveHash = UsuarioRepository.hashClave("martini123")
    )

    // Se guarda el dispatcher para poder "adelantar el reloj" en la prueba del login correcto
    var principal: TestDispatcher = UnconfinedTestDispatcher()
    beforeTest {
        principal = UnconfinedTestDispatcher()
        Dispatchers.setMain(principal)
    }
    afterTest { Dispatchers.resetMain() }

    // Crea el ViewModel con una Application simulada y los repositorios que pida cada prueba
    fun crearViewModel(
        usuarios: UsuarioRepository = mockk(relaxed = true),
        preferencias: PreferenciasRepository = mockk(relaxed = true)
    ) = AuthViewModel(mockk<Application>(relaxed = true), usuarios, preferencias)

    // Llena el Registro con datos válidos (el RUT se deja vacío porque es opcional)
    fun llenarRegistroValido(vm: AuthViewModel) {
        vm.onNombreChange("Ana Pérez")
        vm.onEmailChange(" Ana@Martini.cl ")
        vm.onTelefonoChange("+56 9 1234 5678")
        vm.onClaveChange("clave123")
        vm.onTipoNegocioChange("Pastelería")
        vm.onTamanoNegocioChange("Pequeño")
        vm.onDireccionChange("Av. Siempre Viva 742")
        vm.onInteresClick("Panadería")
    }

    // ===================== LOGIN =====================

    "al crearse asegura que exista la cuenta demo" {
        val usuarios = mockk<UsuarioRepository>(relaxed = true)

        crearViewModel(usuarios)

        coVerify(exactly = 1) { usuarios.crearUsuarioDemoSiNoExiste() }
    }

    "un login con campos vacíos marca los errores y no consulta la base de datos" {
        val usuarios = mockk<UsuarioRepository>(relaxed = true)
        val vm = crearViewModel(usuarios)
        var entro = false

        vm.iniciarSesion { entro = true }

        vm.login.value.emailError.shouldNotBeNull()
        vm.login.value.claveError.shouldNotBeNull()
        entro shouldBe false
        coVerify(exactly = 0) { usuarios.validarCredenciales(any(), any()) }
    }

    "con credenciales incorrectas muestra el error general y no entra" {
        val usuarios = mockk<UsuarioRepository>(relaxed = true)
        coEvery { usuarios.validarCredenciales(any(), any()) } returns null
        val vm = crearViewModel(usuarios)
        vm.onLoginEmailChange("demo@martini.cl")
        vm.onLoginClaveChange("claveMala")

        vm.iniciarSesion { }

        vm.login.value.loginError.shouldNotBeNull() shouldStartWith "Correo o contraseña incorrectos"
        vm.login.value.cargando shouldBe false

        // Al volver a escribir, el error desaparece
        vm.onLoginClaveChange("martini123")
        vm.login.value.loginError.shouldBeNull()
    }

    "un login correcto guarda la sesión, muestra la barra de carga y luego entra a la app" {
        val usuarios = mockk<UsuarioRepository>(relaxed = true)
        val preferencias = mockk<PreferenciasRepository>(relaxed = true)
        coEvery { usuarios.validarCredenciales("demo@martini.cl", "martini123") } returns camila
        val vm = crearViewModel(usuarios, preferencias)
        vm.onLoginEmailChange("demo@martini.cl")
        vm.onLoginClaveChange("martini123")
        var entro = false

        vm.iniciarSesion { entro = true }

        // Antes de la pausa animada de 1,5 segundos
        vm.login.value.ingresando shouldBe true
        coVerify(exactly = 1) { preferencias.guardarSesion("demo@martini.cl") }
        entro shouldBe false

        // Se adelanta el reloj de prueba: la pausa termina al instante
        principal.scheduler.advanceUntilIdle()
        entro shouldBe true
        vm.login.value shouldBe LoginUiState()   // el formulario queda limpio
    }

    // ===================== REGISTRO =====================

    "un registro vacío marca los errores, deja el mensaje y no guarda nada" {
        val usuarios = mockk<UsuarioRepository>(relaxed = true)
        val vm = crearViewModel(usuarios)

        vm.registrar { }

        val registro = vm.registro.value
        registro.errores.hayErrores() shouldBe true
        registro.errores.nombre.shouldNotBeNull()
        registro.errores.email.shouldNotBeNull()
        registro.errores.intereses.shouldNotBeNull()
        registro.mensaje shouldBe "Revisa los campos marcados en rojo"
        coVerify(exactly = 0) { usuarios.registrar(any()) }

        vm.consumirMensajeRegistro()
        vm.registro.value.mensaje.shouldBeNull()
    }

    "tocar un interés lo agrega y volver a tocarlo lo quita" {
        val vm = crearViewModel()

        vm.onInteresClick("Panadería")
        vm.onInteresClick("Heladería")
        vm.registro.value.intereses shouldBe setOf("Panadería", "Heladería")

        vm.onInteresClick("Panadería")
        vm.registro.value.intereses shouldBe setOf("Heladería")
    }

    "si el correo ya está registrado no crea la cuenta y marca el error del correo" {
        val usuarios = mockk<UsuarioRepository>(relaxed = true)
        coEvery { usuarios.existeEmail(any()) } returns true
        val vm = crearViewModel(usuarios)
        llenarRegistroValido(vm)

        vm.registrar { }

        vm.registro.value.errores.email shouldBe "Este correo ya está registrado"
        vm.registro.value.guardando shouldBe false
        coVerify(exactly = 0) { usuarios.registrar(any()) }
    }

    "un registro válido guarda al usuario con la clave cifrada y abre la sesión" {
        val usuarios = mockk<UsuarioRepository>(relaxed = true)
        val preferencias = mockk<PreferenciasRepository>(relaxed = true)
        coEvery { usuarios.existeEmail(any()) } returns false
        val vm = crearViewModel(usuarios, preferencias)
        llenarRegistroValido(vm)
        var registrado = false

        vm.registrar { registrado = true }

        // El correo se guarda limpio y la contraseña solo como hash SHA-256
        coVerify(exactly = 1) {
            usuarios.registrar(match {
                it.email == "ana@martini.cl" &&
                    it.claveHash == UsuarioRepository.hashClave("clave123") &&
                    it.intereses == "Panadería"
            })
        }
        coVerify(exactly = 1) { preferencias.guardarSesion("ana@martini.cl") }
        registrado shouldBe true
    }
})

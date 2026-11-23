package cl.duoc.mastermartini.viewmodel

import android.app.Application
import cl.duoc.mastermartini.data.entity.UsuarioEntity
import cl.duoc.mastermartini.data.repository.PreferenciasRepository
import cl.duoc.mastermartini.data.repository.UsuarioRepository
import cl.duoc.mastermartini.ui.state.PerfilUiState
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE PerfilViewModel (Kotest + MockK)
// Room y DataStore se reemplazan por mocks. No se prueban aquí las funciones
// de foto (galería y cámara): necesitan imágenes y archivos reales de Android,
// por eso se comprueban a mano en el emulador.
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModelTest : StringSpec({

    val camila = UsuarioEntity(
        id = 1, nombre = "Camila Soto", email = "demo@martini.cl", telefono = "+56 9 5555 1234",
        rut = "12.345.678-5", tipoNegocio = "Pastelería", tamanoNegocio = "Pequeño",
        direccion = "Av. Ejemplo 123, Santiago", intereses = "Panadería,Pastelería",
        claveHash = UsuarioRepository.hashClave("martini123")
    )

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    // DataStore simulado: sesión iniciada (o no) y texto grande activado.
    // flowOf(...) crea un Flow que entrega ese valor, como lo haría DataStore.
    fun preferenciasSimuladas(email: String? = "demo@martini.cl"): PreferenciasRepository {
        val preferencias = mockk<PreferenciasRepository>(relaxed = true)
        every { preferencias.emailSesion } returns flowOf(email)
        every { preferencias.textoGrande } returns flowOf(true)
        every { preferencias.reducirMovimiento } returns flowOf(false)
        every { preferencias.subtitulos } returns flowOf(true)
        return preferencias
    }

    // Room simulado: el correo de la cuenta demo devuelve a Camila
    fun crearViewModel(
        usuarios: UsuarioRepository = mockk(relaxed = true),
        preferencias: PreferenciasRepository = preferenciasSimuladas()
    ): PerfilViewModel {
        coEvery { usuarios.buscarPorEmail("demo@martini.cl") } returns camila
        return PerfilViewModel(mockk<Application>(relaxed = true), usuarios, preferencias)
    }

    "cargarPerfil lee el correo de la sesión y trae al usuario desde Room" {
        val vm = crearViewModel()

        vm.cargarPerfil()

        vm.estado.value.usuario shouldBe camila
        vm.estado.value.cargando shouldBe false
    }

    "sin sesión iniciada el perfil queda vacío y no se consulta Room" {
        val usuarios = mockk<UsuarioRepository>(relaxed = true)
        val vm = crearViewModel(usuarios, preferenciasSimuladas(email = null))

        vm.cargarPerfil()

        vm.estado.value.usuario.shouldBeNull()
        coVerify(exactly = 0) { usuarios.buscarPorEmail(any()) }
    }

    "el rol de administrador y el texto grande se leen desde DataStore" {
        val vm = crearViewModel()

        runTest {
            // Los StateFlow con WhileSubscribed solo se actualizan si alguien los observa,
            // igual que en la app cuando la pantalla usa collectAsState()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.esAdministrador.collect { } }
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.textoGrande.collect { } }

            vm.esAdministrador.value shouldBe true   // demo@martini.cl está en Roles
            vm.textoGrande.value shouldBe true
        }
    }

    "editar con el nombre vacío marca el error y no guarda en Room" {
        val usuarios = mockk<UsuarioRepository>(relaxed = true)
        val vm = crearViewModel(usuarios)
        vm.cargarPerfil()

        vm.iniciarEdicion()
        vm.estado.value.editando shouldBe true
        vm.estado.value.nombre shouldBe "Camila Soto"   // el formulario parte con los datos actuales

        vm.onNombreChange("")
        vm.guardarCambios()

        vm.estado.value.nombreError.shouldNotBeNull()
        coVerify(exactly = 0) { usuarios.actualizar(any()) }

        vm.cancelarEdicion()
        vm.estado.value.editando shouldBe false
    }

    "guardar cambios válidos actualiza Room y avisa con un mensaje" {
        val usuarios = mockk<UsuarioRepository>(relaxed = true)
        val vm = crearViewModel(usuarios)
        vm.cargarPerfil()
        vm.iniciarEdicion()
        vm.onNombreChange(" Camila Rojas ")
        vm.onTelefonoChange("+56 9 8765 4321")
        vm.onDireccionChange("Calle Nueva 456")

        vm.guardarCambios()

        coVerify(exactly = 1) {
            usuarios.actualizar(match { it.nombre == "Camila Rojas" && it.direccion == "Calle Nueva 456" })
        }
        vm.estado.value.editando shouldBe false
        vm.estado.value.mensaje shouldBe "Perfil actualizado"

        vm.consumirMensaje()
        vm.estado.value.mensaje.shouldBeNull()
    }

    "los interruptores de accesibilidad se guardan en DataStore" {
        val preferencias = preferenciasSimuladas()
        val vm = crearViewModel(preferencias = preferencias)

        vm.setTextoGrande(true)
        vm.setReducirMovimiento(true)
        vm.setSubtitulos(false)

        coVerify(exactly = 1) { preferencias.setTextoGrande(true) }
        coVerify(exactly = 1) { preferencias.setReducirMovimiento(true) }
        coVerify(exactly = 1) { preferencias.setSubtitulos(false) }
    }

    "cerrar sesión borra la sesión, limpia el perfil y avisa a la pantalla" {
        val preferencias = preferenciasSimuladas()
        val vm = crearViewModel(preferencias = preferencias)
        vm.cargarPerfil()
        var listo = false

        vm.cerrarSesion { listo = true }

        coVerify(exactly = 1) { preferencias.cerrarSesion() }
        vm.estado.value shouldBe PerfilUiState()
        listo shouldBe true
    }
})

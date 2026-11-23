package cl.duoc.mastermartini.viewmodel

import android.app.Application
import cl.duoc.mastermartini.data.entity.FavoritoEntity
import cl.duoc.mastermartini.data.entity.ProgresoEntity
import cl.duoc.mastermartini.data.repository.FavoritoRepository
import cl.duoc.mastermartini.data.repository.ProgresoRepository
import cl.duoc.mastermartini.navigation.NavigationEvent
import cl.duoc.mastermartini.navigation.Screen
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

// ─────────────────────────────────────────────────────────────
// PRUEBAS DE MainViewModel: favoritos, progreso y navegación (Kotest + MockK)
// Los repositorios de Room se reemplazan por mocks.
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest : StringSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    // Room simulado: el contenido 1 es favorito; el 5 está completado y el 6 quedó pendiente
    fun crearViewModel(
        favoritos: FavoritoRepository = mockk(relaxed = true),
        progreso: ProgresoRepository = mockk(relaxed = true)
    ): MainViewModel {
        every { favoritos.obtenerTodos() } returns flowOf(listOf(FavoritoEntity(1)))
        every { progreso.obtenerTodos() } returns flowOf(
            listOf(ProgresoEntity(5, completado = true), ProgresoEntity(6, completado = false))
        )
        return MainViewModel(mockk<Application>(relaxed = true), favoritos, progreso)
    }

    "al crearse lee los favoritos y el progreso guardados en Room" {
        val vm = crearViewModel()

        vm.esFavorito(1) shouldBe true
        vm.esFavorito(2) shouldBe false
        vm.estaCompletado(5) shouldBe true
        vm.estaCompletado(6) shouldBe false   // solo cuentan los completados
    }

    "alternarFavorito quita un favorito existente y agrega uno nuevo" {
        val favoritos = mockk<FavoritoRepository>(relaxed = true)
        val vm = crearViewModel(favoritos = favoritos)

        vm.alternarFavorito(1)   // ya era favorito → se elimina
        vm.alternarFavorito(2)   // no lo era → se agrega

        coVerify(exactly = 1) { favoritos.eliminar(1) }
        coVerify(exactly = 1) { favoritos.insertar(2) }
    }

    "alternarProgreso deja pendiente lo completado y completa lo pendiente" {
        val progreso = mockk<ProgresoRepository>(relaxed = true)
        val vm = crearViewModel(progreso = progreso)

        vm.alternarProgreso(5)   // estaba completado
        vm.alternarProgreso(7)   // no estaba completado

        coVerify(exactly = 1) { progreso.marcarPendiente(5) }
        coVerify(exactly = 1) { progreso.marcarCompletado(7) }
    }

    "navigateTo, navigateBack y navigateUp emiten sus eventos de navegación" {
        val vm = crearViewModel()

        runTest {
            // Un "oyente" que guarda cada evento que emite el ViewModel
            val eventos = mutableListOf<NavigationEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.navigationEvents.toList(eventos) }

            vm.navigateTo(Screen.Cursos)
            vm.navigateBack()
            vm.navigateUp()

            eventos shouldBe listOf(
                NavigationEvent.NavigateTo(route = Screen.Cursos, popUpToRoute = Screen.Home, singleTop = true),
                NavigationEvent.PopBackStack,
                NavigationEvent.NavigateUp
            )
        }
    }

    "navegarLimpiando borra el historial (cerrar sesión) y navigateToRoute usa la ruta escrita" {
        val vm = crearViewModel()

        runTest {
            val eventos = mutableListOf<NavigationEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.navigationEvents.toList(eventos) }

            vm.navegarLimpiando(destino = Screen.Login, limpiarHasta = Screen.Home)
            vm.navigateToRoute("detalle/3")

            eventos shouldBe listOf(
                NavigationEvent.NavigateTo(
                    route = Screen.Login, popUpToRoute = Screen.Home, inclusive = true, singleTop = true
                ),
                NavigationEvent.NavigateToRoute("detalle/3")
            )
        }
    }
})

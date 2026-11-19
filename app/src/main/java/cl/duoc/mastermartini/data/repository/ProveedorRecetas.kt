package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.remote.ApiConfig

// ─────────────────────────────────────────────────────────────
// PROVEEDOR DEL REPOSITORIO DE RECETAS
// Decide, en un solo lugar, qué versión usa la app según
// ApiConfig.USAR_DATOS_SIMULADOS.
// Cada versión se crea una sola vez ("by lazy"), así la pantalla de lista
// y la de detalle comparten los mismos datos.
// ─────────────────────────────────────────────────────────────
object ProveedorRecetas {

    private val simulado: RecetaRepository by lazy { RecetaFakeRepository() }
    private val remoto: RecetaRepository by lazy { RecetaRemoteRepository() }

    fun repositorio(): RecetaRepository =
        if (ApiConfig.USAR_DATOS_SIMULADOS) simulado else remoto
}

package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.remote.ApiService
import cl.duoc.mastermartini.data.remote.RetrofitInstance
import cl.duoc.mastermartini.model.Post

// ─────────────────────────────────────────────────────────────
// GUÍA 14 · REPOSITORIO DE POSTS
// Único lugar que conversa con la API. El ViewModel le pide datos aquí,
// sin saber si vienen de Internet, de Room o de datos de prueba.
// El servicio se recibe como parámetro (con valor por defecto) para poder
// reemplazarlo por uno falso en las pruebas unitarias (Bloque 5).
// ─────────────────────────────────────────────────────────────
class PostRepository(
    private val api: ApiService = RetrofitInstance.api
) {

    suspend fun getPosts(): List<Post> = api.getPosts()
}

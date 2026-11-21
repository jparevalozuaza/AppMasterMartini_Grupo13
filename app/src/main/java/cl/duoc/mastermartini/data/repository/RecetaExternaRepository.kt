package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.model.RecetaExterna

// ─────────────────────────────────────────────────────────────
// CONTRATO DEL REPOSITORIO DE LA API EXTERNA
// Igual idea que RecetaRepository: el ViewModel solo conoce este contrato,
// así en las pruebas (Bloque 5) se le puede entregar una versión falsa.
// Si algo falla, las funciones lanzan una excepción que el ViewModel atrapa.
// ─────────────────────────────────────────────────────────────
interface RecetaExternaRepository {

    // GET: primeras recetas de la API
    suspend fun obtenerRecetas(): List<RecetaExterna>

    // GET con búsqueda por texto (si viene vacío, devuelve todas)
    suspend fun buscarRecetas(texto: String): List<RecetaExterna>

    // POST: devuelve la receta creada, con el id que asignó la API
    suspend fun crearReceta(receta: RecetaExterna): RecetaExterna

    // PUT: devuelve la receta actualizada, o null si la API no conoce ese id (404)
    suspend fun actualizarReceta(receta: RecetaExterna): RecetaExterna?

    // DELETE: true si la API la eliminó, false si no conoce ese id (404)
    suspend fun eliminarReceta(id: Int): Boolean
}

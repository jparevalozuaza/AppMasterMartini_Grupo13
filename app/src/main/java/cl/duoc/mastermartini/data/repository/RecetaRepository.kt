package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.model.Receta

// ─────────────────────────────────────────────────────────────
// CONTRATO DEL REPOSITORIO DE RECETAS
// Una "interface" solo dice QUÉ se puede hacer, no CÓMO.
// Hay dos formas de cumplirla:
//   • RecetaRemoteRepository → usa el microservicio real (Retrofit)
//   • RecetaFakeRepository   → usa datos simulados en memoria
// El ViewModel solo conoce este contrato: no sabe (ni le importa) cuál de
// las dos está usando. Por eso se pueden intercambiar sin tocar pantallas,
// y en las pruebas (Bloque 5) se le puede entregar una versión falsa.
//
// Todas las funciones son "suspend": pueden tardar (red) y se ejecutan
// dentro de una corrutina. Si algo falla, lanzan una excepción que el
// ViewModel atrapa con try/catch.
// ─────────────────────────────────────────────────────────────
interface RecetaRepository {

    // Todas las recetas
    suspend fun obtenerRecetas(): List<Receta>

    // Una receta por id, o null si no existe
    suspend fun obtenerReceta(id: Int): Receta?

    // Crea una receta (con id = 0) y devuelve la receta guardada, ya con su id
    suspend fun crearReceta(receta: Receta): Receta

    // Reemplaza una receta existente; devuelve null si ese id no existe
    suspend fun actualizarReceta(receta: Receta): Receta?

    // Elimina una receta; devuelve true si existía y se eliminó
    suspend fun eliminarReceta(id: Int): Boolean
}

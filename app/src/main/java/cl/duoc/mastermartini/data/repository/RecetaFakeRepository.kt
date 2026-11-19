package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.model.Receta
import kotlinx.coroutines.delay
import java.io.IOException

// ─────────────────────────────────────────────────────────────
// REPOSITORIO SIMULADO: cumple el mismo contrato, pero guarda las recetas
// en una lista en memoria. Sirve para:
//   • desarrollar y probar las pantallas sin encender el servidor;
//   • las pruebas unitarias del Bloque 5 (datos conocidos y sin red).
// Los cambios se pierden al cerrar la app (no hay base de datos).
// ─────────────────────────────────────────────────────────────
class RecetaFakeRepository(
    recetasIniciales: List<Receta> = RecetasSimuladas.iniciales,
    private val demoraMs: Long = 600          // imita el tiempo de respuesta de la red
) : RecetaRepository {

    // Copia modificable de las recetas (la lista original no se toca)
    private val recetas = recetasIniciales.toMutableList()

    // Si es true, cada operación falla como si no hubiera conexión.
    // Permite ver y probar el estado de error.
    var simularError: Boolean = false

    // Espera un momento y, si corresponde, falla
    private suspend fun simularRed() {
        delay(demoraMs)
        if (simularError) throw IOException("Error de conexión simulado")
    }

    override suspend fun obtenerRecetas(): List<Receta> {
        simularRed()
        return recetas.toList()                      // se entrega una copia
    }

    override suspend fun obtenerReceta(id: Int): Receta? {
        simularRed()
        return recetas.find { it.id == id }
    }

    override suspend fun crearReceta(receta: Receta): Receta {
        simularRed()
        // Igual que MySQL: el id nuevo es el mayor existente + 1
        val nueva = receta.copy(id = (recetas.maxOfOrNull { it.id } ?: 0) + 1)
        recetas.add(nueva)
        return nueva
    }

    override suspend fun actualizarReceta(receta: Receta): Receta? {
        simularRed()
        val posicion = recetas.indexOfFirst { it.id == receta.id }
        if (posicion == -1) return null              // no existe
        recetas[posicion] = receta
        return receta
    }

    override suspend fun eliminarReceta(id: Int): Boolean {
        simularRed()
        return recetas.removeAll { it.id == id }     // true si había alguna con ese id
    }
}

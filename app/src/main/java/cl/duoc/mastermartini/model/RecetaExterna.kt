package cl.duoc.mastermartini.model

// ─────────────────────────────────────────────────────────────
// RECETA EXTERNA: una receta del mundo que viene de la API pública DummyJSON.
// Es un modelo SEPARADO de Receta (las recetas propias de Master Martini),
// para no mezclar los datos externos con los del negocio.
// ─────────────────────────────────────────────────────────────
data class RecetaExterna(
    val id: Int = 0,                          // 0 = receta nueva (aún no enviada)
    val nombre: String,
    val cocina: String,                       // tipo de cocina: Italian, Mexican…
    val dificultad: String,                   // Easy, Medium, Hard
    val minutosPreparacion: Int,
    val minutosCoccion: Int = 0,
    val porciones: Int = 0,
    val calorias: Int = 0,                    // calorías por porción
    val valoracion: Double = 0.0,             // de 0 a 5
    val ingredientes: List<String>,
    val instrucciones: List<String> = emptyList(),
    // true si la receta se creó en la app: DummyJSON no la guarda,
    // así que solo existe en la lista que muestra la pantalla
    val soloLocal: Boolean = false
)

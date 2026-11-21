package cl.duoc.mastermartini.data.model

import cl.duoc.mastermartini.model.RecetaExterna

// ─────────────────────────────────────────────────────────────
// MODELOS DE COMUNICACIÓN DE LA API EXTERNA (DummyJSON Recipes)
// Los nombres de las propiedades son los del JSON de DummyJSON (en inglés),
// porque Gson los usa para leer y escribir el JSON. Todo admite null:
// si la API omite un campo, la app no se cae.
// ─────────────────────────────────────────────────────────────

// Una receta tal como la entrega DummyJSON
data class RecetaExternaDto(
    val id: Int? = null,
    val name: String? = null,
    val ingredients: List<String>? = null,
    val instructions: List<String>? = null,
    val prepTimeMinutes: Int? = null,
    val cookTimeMinutes: Int? = null,
    val servings: Int? = null,
    val difficulty: String? = null,
    val cuisine: String? = null,
    val caloriesPerServing: Int? = null,
    val rating: Double? = null,
    val isDeleted: Boolean? = null            // solo viene en la respuesta de DELETE
)

// GET /recipes no devuelve una lista directa, sino un objeto que la envuelve:
//   { "recipes": [ ... ], "total": 50, "skip": 0, "limit": 30 }
data class RecetasExternasRespuesta(
    val recipes: List<RecetaExternaDto>? = null,
    val total: Int? = null,
    val skip: Int? = null,
    val limit: Int? = null
)

// De la API a la app. "respaldo" se usa cuando la respuesta trae pocos campos
// (por ejemplo, POST devuelve solo lo enviado + el id): lo que falte se toma de ahí.
fun RecetaExternaDto.aRecetaExterna(respaldo: RecetaExterna? = null): RecetaExterna = RecetaExterna(
    id = id ?: respaldo?.id ?: 0,
    nombre = name ?: respaldo?.nombre.orEmpty(),
    cocina = cuisine ?: respaldo?.cocina.orEmpty(),
    dificultad = difficulty ?: respaldo?.dificultad.orEmpty(),
    minutosPreparacion = prepTimeMinutes ?: respaldo?.minutosPreparacion ?: 0,
    minutosCoccion = cookTimeMinutes ?: respaldo?.minutosCoccion ?: 0,
    porciones = servings ?: respaldo?.porciones ?: 0,
    calorias = caloriesPerServing ?: respaldo?.calorias ?: 0,
    valoracion = rating ?: respaldo?.valoracion ?: 0.0,
    ingredientes = ingredients ?: respaldo?.ingredientes.orEmpty(),
    instrucciones = instructions ?: respaldo?.instrucciones.orEmpty(),
    soloLocal = respaldo?.soloLocal ?: false
)

// De la app a la API (no se envía el id: va en la ruta o lo asigna la API)
fun RecetaExterna.aDto(): RecetaExternaDto = RecetaExternaDto(
    name = nombre,
    ingredients = ingredientes,
    instructions = instrucciones,
    prepTimeMinutes = minutosPreparacion,
    cookTimeMinutes = minutosCoccion,
    servings = porciones,
    difficulty = dificultad,
    cuisine = cocina,
    caloriesPerServing = calorias
)

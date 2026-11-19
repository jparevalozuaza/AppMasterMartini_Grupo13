package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.model.Receta

// ─────────────────────────────────────────────────────────────
// DATOS SIMULADOS (ficticios, solo para desarrollo y pruebas)
// Permiten trabajar las pantallas sin tener el servidor encendido,
// como pide el documento de Responsabilidades (Bloque 2).
// Son las mismas recetas de ejemplo que la app tenía en Room.
// ─────────────────────────────────────────────────────────────
object RecetasSimuladas {

    val iniciales: List<Receta> = listOf(
        Receta(
            id = 1,
            titulo = "Brownie de chocolate",
            descripcion = "Brownie húmedo con intenso sabor a chocolate.",
            categoria = "Pastelería",
            tiempoPreparacion = 45,
            ingredientes = "Chocolate, mantequilla, azúcar, huevos y harina.",
            preparacion = "Derretir el chocolate con la mantequilla. Mezclar con el azúcar y los huevos, incorporar la harina y hornear hasta que el centro quede húmedo."
        ),
        Receta(
            id = 2,
            titulo = "Galletas con chips de chocolate",
            descripcion = "Galletas caseras crujientes por fuera y suaves por dentro.",
            categoria = "Pastelería",
            tiempoPreparacion = 30,
            ingredientes = "Harina, mantequilla, azúcar, huevo y chips de chocolate.",
            preparacion = "Mezclar los ingredientes, agregar los chips de chocolate, formar las galletas y hornear hasta que los bordes estén dorados."
        ),
        Receta(
            id = 3,
            titulo = "Trufas de chocolate",
            descripcion = "Pequeñas trufas ideales para compartir o vender.",
            categoria = "Chocolatería",
            tiempoPreparacion = 25,
            ingredientes = "Chocolate, crema de leche y cacao en polvo.",
            preparacion = "Calentar la crema, mezclarla con el chocolate picado, refrigerar la mezcla, formar bolitas y cubrirlas con cacao."
        ),
        Receta(
            id = 4,
            titulo = "Pan de molde casero",
            descripcion = "Pan suave para preparar sándwiches.",
            categoria = "Panadería",
            tiempoPreparacion = 120,
            ingredientes = "Harina, agua, levadura, sal, azúcar y aceite.",
            preparacion = "Mezclar los ingredientes, amasar, dejar fermentar, colocar en un molde y hornear hasta que el pan esté dorado."
        ),
        Receta(
            id = 5,
            titulo = "Helado de chocolate",
            descripcion = "Helado cremoso con sabor a chocolate.",
            categoria = "Heladería",
            tiempoPreparacion = 30,
            ingredientes = "Leche, crema, azúcar y chocolate.",
            preparacion = "Calentar la leche con el azúcar y el chocolate, dejar enfriar, incorporar la crema y llevar a congelación, mezclando periódicamente."
        )
    )
}

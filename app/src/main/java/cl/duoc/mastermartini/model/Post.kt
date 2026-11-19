package cl.duoc.mastermartini.model

// ─────────────────────────────────────────────────────────────
// GUÍA 14 · MODELO
// Representa un "post" tal como lo entrega la API JSONPlaceholder.
// Los nombres de las propiedades deben coincidir con las claves del JSON:
// Gson usa esos nombres para convertir el texto JSON en un objeto Kotlin.
//   { "userId": 1, "id": 1, "title": "...", "body": "..." }
// ─────────────────────────────────────────────────────────────
data class Post(
    val userId: Int,   // ID del usuario que creó el post
    val id: Int,       // ID del post
    val title: String, // Título del post
    val body: String   // Cuerpo o contenido del post
)

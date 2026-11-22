package cl.duoc.mastermartini.utils

// ─────────────────────────────────────────────────────────────
// VALIDACIONES DEL FORMULARIO DE RECETAS
// Misma regla que Validation.kt: cada función recibe el texto escrito y devuelve
//   null   → el dato es válido
//   String → el mensaje de error que verá el usuario bajo el campo
//
// Los máximos de caracteres respetan al backend: en MySQL cada texto se guarda
// como VARCHAR(255), así que nunca se envía un texto más largo que eso.
// No dependen de Compose ni de Android: se pueden probar con pruebas unitarias.
// ─────────────────────────────────────────────────────────────

const val MAX_TITULO_RECETA = 60
const val MAX_CATEGORIA_RECETA = 30
const val MAX_DESCRIPCION_RECETA = 200
const val MAX_TEXTO_LARGO_RECETA = 255          // ingredientes y preparación
const val MAX_MINUTOS_RECETA = 1440             // 24 horas

fun validarTituloReceta(titulo: String): String? {
    val limpio = titulo.trim()
    return when {
        limpio.isEmpty() -> "Ingresa el título de la receta"
        limpio.length < 3 -> "El título debe tener al menos 3 caracteres"
        limpio.length > MAX_TITULO_RECETA -> "El título no puede superar $MAX_TITULO_RECETA caracteres"
        else -> null
    }
}

fun validarCategoriaReceta(categoria: String): String? {
    val limpio = categoria.trim()
    return when {
        limpio.isEmpty() -> "Ingresa una categoría. Ejemplo: Pastelería"
        limpio.length > MAX_CATEGORIA_RECETA -> "La categoría no puede superar $MAX_CATEGORIA_RECETA caracteres"
        else -> null
    }
}

// El tiempo llega como texto (lo que se escribió en el campo) y debe ser un número entero
fun validarTiempoPreparacion(tiempo: String): String? {
    val minutos = tiempo.trim().toIntOrNull()
    return when {
        tiempo.isBlank() -> "Ingresa el tiempo de preparación en minutos"
        minutos == null -> "Usa solo números. Ejemplo: 45"
        minutos < 1 -> "El tiempo debe ser de al menos 1 minuto"
        minutos > MAX_MINUTOS_RECETA -> "El tiempo no puede superar $MAX_MINUTOS_RECETA minutos (24 horas)"
        else -> null
    }
}

fun validarDescripcionReceta(descripcion: String): String? {
    val limpio = descripcion.trim()
    return when {
        limpio.isEmpty() -> "Escribe una descripción breve"
        limpio.length < 10 -> "La descripción debe tener al menos 10 caracteres"
        limpio.length > MAX_DESCRIPCION_RECETA -> "La descripción no puede superar $MAX_DESCRIPCION_RECETA caracteres"
        else -> null
    }
}

// Sirve para ingredientes y preparación: solo cambia el nombre del campo en el mensaje
fun validarTextoLargoReceta(texto: String, campo: String): String? {
    val limpio = texto.trim()
    return when {
        limpio.isEmpty() -> "Completa $campo"
        limpio.length < 10 -> "Detalla $campo en al menos 10 caracteres"
        limpio.length > MAX_TEXTO_LARGO_RECETA -> "Este campo no puede superar $MAX_TEXTO_LARGO_RECETA caracteres"
        else -> null
    }
}

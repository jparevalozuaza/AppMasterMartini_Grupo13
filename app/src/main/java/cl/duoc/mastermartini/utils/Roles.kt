package cl.duoc.mastermartini.utils

import cl.duoc.mastermartini.data.repository.UsuarioRepository

// ─────────────────────────────────────────────────────────────
// ROLES DE LA APP
// Hay dos perfiles:
//   • Administrador → puede crear, editar y eliminar recetas.
//   • Usuario       → solo consulta y busca recetas.
// Un correo es administrador si está en esta lista.
//
// SEGURIDAD: aquí se guardan SOLO correos, nunca contraseñas.
// El repositorio es público; cada administrador entra con la contraseña
// de su cuenta, que se guarda cifrada (hash SHA-256) en Room.
// Además, esconder botones es una medida de interfaz: la protección
// real de los endpoints correspondería al backend.
// ─────────────────────────────────────────────────────────────
object Roles {

    // Para agregar un administrador: escribir su correo aquí, en minúsculas.
    // La cuenta debe existir (creada desde "Crear cuenta").
    private val CORREOS_ADMINISTRADORES: Set<String> = setOf(
        UsuarioRepository.EMAIL_DEMO        // demo@martini.cl
    )

    // true si el correo de la sesión pertenece a un administrador.
    // Se ignoran mayúsculas y espacios: "Demo@Martini.cl " también cuenta.
    fun esAdministrador(email: String?): Boolean =
        email != null && email.trim().lowercase() in CORREOS_ADMINISTRADORES
}

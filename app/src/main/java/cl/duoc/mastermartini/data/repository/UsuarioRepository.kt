package cl.duoc.mastermartini.data.repository

import cl.duoc.mastermartini.data.dao.UsuarioDao
import cl.duoc.mastermartini.data.entity.UsuarioEntity
import java.security.MessageDigest

// ─────────────────────────────────────────────────────────────
// REPOSITORIO DE USUARIOS
// Es el único lugar que conversa con UsuarioDao (Room).
// Los ViewModels piden datos aquí; nunca tocan Room directamente.
// ─────────────────────────────────────────────────────────────
class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    // Los correos se guardan y buscan siempre en minúsculas y sin espacios
    suspend fun buscarPorEmail(email: String): UsuarioEntity? =
        usuarioDao.buscarPorEmail(email.trim().lowercase())

    suspend fun existeEmail(email: String): Boolean =
        buscarPorEmail(email) != null

    suspend fun registrar(usuario: UsuarioEntity) {
        usuarioDao.insertar(usuario)
    }

    suspend fun actualizar(usuario: UsuarioEntity) {
        usuarioDao.actualizar(usuario)
    }

    // Devuelve el usuario si el correo existe y la contraseña coincide; si no, null
    suspend fun validarCredenciales(email: String, clave: String): UsuarioEntity? {
        val usuario = buscarPorEmail(email) ?: return null
        return if (usuario.claveHash == hashClave(clave)) usuario else null
    }

    // Crea la cuenta demo del mockup la primera vez que se abre la app
    suspend fun crearUsuarioDemoSiNoExiste() {
        if (buscarPorEmail(EMAIL_DEMO) == null) {
            usuarioDao.insertar(
                UsuarioEntity(
                    nombre = "Camila Soto",
                    email = EMAIL_DEMO,
                    telefono = "+56 9 5555 1234",
                    rut = "12.345.678-5",
                    tipoNegocio = "Pastelería",
                    tamanoNegocio = "Pequeño",
                    direccion = "Av. Ejemplo 123, Santiago",
                    intereses = "Panadería,Pastelería",
                    claveHash = hashClave(CLAVE_DEMO)
                )
            )
        }
    }

    companion object {
        // Datos ficticios para la demostración académica
        const val EMAIL_DEMO = "demo@martini.cl"
        const val CLAVE_DEMO = "martini123"

        // SEGURIDAD: se guarda un "hash" SHA-256 de la contraseña, no la contraseña.
        // Un hash es una huella irreversible: sirve para comparar, pero no para leer la clave.
        fun hashClave(clave: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(clave.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}

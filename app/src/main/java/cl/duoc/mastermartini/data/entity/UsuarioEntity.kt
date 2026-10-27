package cl.duoc.mastermartini.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// ─────────────────────────────────────────────────────────────
// TABLA "usuarios" de Room (guía sección 9.1: UserProfileEntity)
// Guarda el perfil creado en Registro y editado en Perfil.
// El índice único en "email" impide registrar dos cuentas con el mismo correo.
// ─────────────────────────────────────────────────────────────
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val telefono: String,
    val rut: String,
    val tipoNegocio: String,
    val tamanoNegocio: String,
    val direccion: String,
    val intereses: String,          // separados por coma: "Panadería,Pastelería"
    val fotoPath: String? = null,   // ruta de la foto de perfil en el almacenamiento interno
    val claveHash: String           // la contraseña NUNCA se guarda en texto plano
)

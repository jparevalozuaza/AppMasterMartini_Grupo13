package cl.duoc.mastermartini.data.remote

// ─────────────────────────────────────────────────────────────
// CONFIGURACIÓN DE LAS CONEXIONES REMOTAS
// Paquete data/remote: todo lo que la app obtiene por Internet (Retrofit).
// data/dao y data/database siguen siendo los datos locales (Room).
//
// Módulos que usarán el backend propio (microservicio de recetas):
//   RecetasScreen y DetalleRecetaScreen → RecetaViewModel → RecetaRepository
//   (versión remota: RecetaRemoteRepository → RecetaApiService → RetrofitInstance).
// El resto de la app (usuarios, perfil, favoritos, progreso) sigue siendo local.
// ─────────────────────────────────────────────────────────────
object ApiConfig {

    // Microservicio de recetas (Spring Boot, puerto 8080, rutas /api/recetas).
    // 10.0.2.2 es la dirección con la que el EMULADOR llega al computador donde corre el backend.
    // Retrofit exige que la URL base termine en "/".
    const val BACKEND_BASE_URL = "http://10.0.2.2:8080/"

    // Tiempo máximo de espera de una respuesta, en segundos.
    const val TIEMPO_ESPERA_SEGUNDOS = 15L

    // Origen de las recetas:
    //   true  → datos simulados en memoria (no necesita el servidor encendido)
    //   false → microservicio real (requiere Docker + backend en ejecución)
    // Para la entrega final debe quedar en false.
    const val USAR_DATOS_SIMULADOS = true
}

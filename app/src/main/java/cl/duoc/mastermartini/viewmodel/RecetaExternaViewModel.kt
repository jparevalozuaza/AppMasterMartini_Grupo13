package cl.duoc.mastermartini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.repository.RecetaExternaRemoteRepository
import cl.duoc.mastermartini.data.repository.RecetaExternaRepository
import cl.duoc.mastermartini.model.RecetaExterna
import cl.duoc.mastermartini.ui.state.RecetaExternaFormUiState
import cl.duoc.mastermartini.ui.state.RecetaExternaUiState
import cl.duoc.mastermartini.utils.validarTiempoPreparacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// ─────────────────────────────────────────────────────────────
// VIEWMODEL DE "RECETAS DEL MUNDO" (API externa DummyJSON)
// Ejecuta el ciclo CRUD completo: GET, POST, PUT y DELETE.
//
// DummyJSON SIMULA los cambios: responde con éxito, pero no guarda nada.
// Por eso la lista que se ve se actualiza aquí, en memoria, con lo que
// responde la API (igual que en la Guía 14 con JSONPlaceholder).
// Las recetas creadas en la app no existen en DummyJSON: si se editan o
// eliminan, la API responde 404 y el cambio se aplica solo en la app.
// ─────────────────────────────────────────────────────────────
class RecetaExternaViewModel(
    private val repository: RecetaExternaRepository = RecetaExternaRemoteRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow<RecetaExternaUiState>(RecetaExternaUiState.Cargando)
    val estado: StateFlow<RecetaExternaUiState> = _estado.asStateFlow()

    // Texto del buscador
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // Mensaje para el Snackbar después de cada operación (null = sin mensaje)
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // Formulario (diálogo) para crear o editar
    private val _formulario = MutableStateFlow(RecetaExternaFormUiState())
    val formulario: StateFlow<RecetaExternaFormUiState> = _formulario.asStateFlow()

    // Receta original que se está editando (para conservar los datos que el formulario no muestra)
    private var recetaEnEdicion: RecetaExterna? = null

    init {
        cargarRecetas()
    }

    // ===================== CONSULTAR (GET) =====================
    fun cargarRecetas() {
        consultar { repository.obtenerRecetas() }
    }

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
    }

    // GET /recipes/search?q=… (con el texto vacío vuelve a la lista completa)
    fun buscar() {
        val texto = _busqueda.value
        consultar { repository.buscarRecetas(texto) }
    }

    // Lógica común de las consultas: Cargando → Exito o Error
    private fun consultar(peticion: suspend () -> List<RecetaExterna>) {
        viewModelScope.launch {
            _estado.value = RecetaExternaUiState.Cargando
            _estado.value = try {
                RecetaExternaUiState.Exito(peticion())
            } catch (e: Exception) {
                RecetaExternaUiState.Error(mensajeDeError(e))
            }
        }
    }

    // ===================== FORMULARIO =====================
    fun abrirFormularioNueva() {
        recetaEnEdicion = null
        _formulario.value = RecetaExternaFormUiState(visible = true)
    }

    fun abrirFormularioEdicion(receta: RecetaExterna) {
        recetaEnEdicion = receta
        _formulario.value = RecetaExternaFormUiState(
            visible = true,
            id = receta.id,
            nombre = receta.nombre,
            cocina = receta.cocina,
            minutos = receta.minutosPreparacion.toString(),
            ingredientes = receta.ingredientes.joinToString(", ")
        )
    }

    fun cerrarFormulario() {
        _formulario.value = RecetaExternaFormUiState()
    }

    fun onNombreChange(valor: String) =
        _formulario.update { it.copy(nombre = valor, nombreError = null) }

    fun onCocinaChange(valor: String) =
        _formulario.update { it.copy(cocina = valor, cocinaError = null) }

    fun onMinutosChange(valor: String) =
        _formulario.update {
            it.copy(minutos = valor.filter { c -> c.isDigit() }.take(4), minutosError = null)
        }

    fun onIngredientesChange(valor: String) =
        _formulario.update { it.copy(ingredientes = valor, ingredientesError = null) }

    // Revisa todos los campos; devuelve true solo si no hay errores
    fun validarFormulario(): Boolean {
        val f = _formulario.value
        val conErrores = f.copy(
            nombreError = when {
                f.nombre.isBlank() -> "Ingresa el nombre de la receta"
                f.nombre.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres"
                else -> null
            },
            cocinaError = if (f.cocina.isBlank()) "Ingresa el tipo de cocina. Ejemplo: Italian" else null,
            minutosError = validarTiempoPreparacion(f.minutos),
            ingredientesError =
                if (separarIngredientes(f.ingredientes).isEmpty()) "Ingresa al menos un ingrediente" else null
        )
        _formulario.value = conErrores
        return listOf(
            conErrores.nombreError, conErrores.cocinaError,
            conErrores.minutosError, conErrores.ingredientesError
        ).all { it == null }
    }

    // ===================== CREAR (POST) Y ACTUALIZAR (PUT) =====================
    fun guardarFormulario() {
        if (!validarFormulario()) return
        val f = _formulario.value
        if (f.guardando) return
        _formulario.update { it.copy(guardando = true) }

        // Se parte de la receta original (al editar) o de una receta nueva con valores por defecto
        val base = recetaEnEdicion ?: RecetaExterna(
            nombre = "", cocina = "", dificultad = "Easy",
            minutosPreparacion = 0, ingredientes = emptyList()
        )
        val receta = base.copy(
            nombre = f.nombre.trim(),
            cocina = f.cocina.trim(),
            minutosPreparacion = f.minutos.toIntOrNull() ?: 0,
            ingredientes = separarIngredientes(f.ingredientes)
        )

        viewModelScope.launch {
            try {
                if (f.esEdicion) actualizar(receta) else crear(receta)
                cerrarFormulario()
            } catch (e: Exception) {
                _formulario.update { it.copy(guardando = false) }
                _mensaje.value = mensajeDeError(e)
            }
        }
    }

    private suspend fun crear(receta: RecetaExterna) {
        val creada = repository.crearReceta(receta)
        val lista = listaActual()
        // DummyJSON entrega siempre el mismo id a las recetas nuevas:
        // se asegura un id único dentro de la lista
        val idUnico = maxOf(creada.id, (lista.maxOfOrNull { it.id } ?: 0) + 1)
        _estado.value = RecetaExternaUiState.Exito(
            listOf(creada.copy(id = idUnico, soloLocal = true)) + lista
        )
        _mensaje.value = "Receta creada (POST exitoso, id ${creada.id} asignado por DummyJSON)"
    }

    private suspend fun actualizar(receta: RecetaExterna) {
        val respuesta = repository.actualizarReceta(receta)
        val final = respuesta?.copy(id = receta.id, soloLocal = receta.soloLocal) ?: receta
        _estado.value = RecetaExternaUiState.Exito(
            listaActual().map { if (it.id == receta.id) final else it }
        )
        _mensaje.value = if (respuesta != null) {
            "Receta actualizada (PUT exitoso en DummyJSON)"
        } else {
            "Receta actualizada solo en la app (DummyJSON no guarda recetas nuevas)"
        }
    }

    // ===================== ELIMINAR (DELETE) =====================
    fun eliminarReceta(receta: RecetaExterna) {
        viewModelScope.launch {
            try {
                val eliminadaEnApi = repository.eliminarReceta(receta.id)
                _estado.value = RecetaExternaUiState.Exito(
                    listaActual().filterNot { it.id == receta.id }
                )
                _mensaje.value = if (eliminadaEnApi) {
                    "Receta eliminada (DELETE exitoso en DummyJSON)"
                } else {
                    "Receta eliminada solo en la app (DummyJSON no la tenía guardada)"
                }
            } catch (e: Exception) {
                _mensaje.value = mensajeDeError(e)
            }
        }
    }

    fun consumirMensaje() {
        _mensaje.value = null
    }

    // ===================== AYUDANTES =====================
    private fun listaActual(): List<RecetaExterna> =
        (_estado.value as? RecetaExternaUiState.Exito)?.recetas ?: emptyList()

    // "Harina, huevos,  azúcar" → ["Harina", "huevos", "azúcar"]
    private fun separarIngredientes(texto: String): List<String> =
        texto.split(",", "\n").map { it.trim() }.filter { it.isNotEmpty() }

    private fun mensajeDeError(e: Exception): String = when (e) {
        is IOException -> "No se pudo conectar con la API externa. Revisa tu conexión a Internet."
        is HttpException -> "La API externa respondió con un error (código ${e.code()})."
        else -> "Ocurrió un error inesperado con la API externa."
    }
}

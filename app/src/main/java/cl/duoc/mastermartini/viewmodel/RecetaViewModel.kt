package cl.duoc.mastermartini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.repository.ProveedorRecetas
import cl.duoc.mastermartini.data.repository.RecetaRepository
import cl.duoc.mastermartini.model.Receta
import cl.duoc.mastermartini.ui.state.RecetaFormUiState
import cl.duoc.mastermartini.ui.state.RecetaUiState
import cl.duoc.mastermartini.ui.state.aFormulario
import cl.duoc.mastermartini.ui.state.aReceta
import cl.duoc.mastermartini.utils.validarCategoriaReceta
import cl.duoc.mastermartini.utils.validarDescripcionReceta
import cl.duoc.mastermartini.utils.validarTextoLargoReceta
import cl.duoc.mastermartini.utils.validarTiempoPreparacion
import cl.duoc.mastermartini.utils.validarTituloReceta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// ─────────────────────────────────────────────────────────────
// VIEWMODEL DE RECETAS (misma estructura que PostViewModel de la Guía 14)
// Mantiene el estado de las pantallas de Recetas y ejecuta el CRUD
// a través del repositorio.
//
// El repositorio llega como PARÁMETRO (con valor por defecto):
//   • la app usa el que entrega ProveedorRecetas (simulado o remoto);
//   • las pruebas del Bloque 5 pueden entregarle uno falso.
// Como todos los parámetros tienen valor por defecto, Kotlin genera un
// constructor vacío y viewModel() en MainActivity lo sigue creando igual.
// ─────────────────────────────────────────────────────────────
class RecetaViewModel(
    private val repository: RecetaRepository = ProveedorRecetas.repositorio()
) : ViewModel() {

    // Estado principal: Cargando, Exito (con la lista) o Error.
    // "_estado" es privado y modificable; "estado" es público y de solo lectura.
    private val _estado = MutableStateFlow<RecetaUiState>(RecetaUiState.Cargando)
    val estado: StateFlow<RecetaUiState> = _estado.asStateFlow()

    // Atajo: solo la lista de recetas (vacía mientras carga o si hay error)
    val recetas: StateFlow<List<Receta>> = estado
        .map { (it as? RecetaUiState.Exito)?.recetas ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Mensaje breve tras crear, editar o eliminar (null = sin mensaje).
    // La pantalla de Recetas lo muestra en un Snackbar.
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // Se ejecuta automáticamente al crear el ViewModel
    init {
        cargarRecetas()
    }

    // ===================== CONSULTAR (GET) =====================
    fun cargarRecetas() {
        viewModelScope.launch {                       // corrutina atada a la vida del ViewModel
            _estado.value = RecetaUiState.Cargando
            _estado.value = try {
                RecetaUiState.Exito(repository.obtenerRecetas())
            } catch (e: Exception) {
                RecetaUiState.Error(mensajeDeError(e))
            }
        }
    }

    // Al entrar a Recetas: si la última carga falló (por ejemplo, el servidor
    // estaba apagado al abrir la app), se intenta de nuevo automáticamente
    fun cargarSiHayError() {
        if (_estado.value is RecetaUiState.Error) cargarRecetas()
    }

    // ===================== FORMULARIO (crear y editar) =====================
    // Estado del formulario: lo que se escribió en cada campo y sus errores
    private val _formulario = MutableStateFlow(RecetaFormUiState())
    val formulario: StateFlow<RecetaFormUiState> = _formulario.asStateFlow()

    // Prepara el formulario al abrir la pantalla:
    //   recetaId = 0 → formulario vacío para una receta nueva
    //   recetaId > 0 → se trae la receta y se cargan sus datos para editarla
    fun prepararFormulario(recetaId: Int) {
        if (recetaId == 0) {
            _formulario.value = RecetaFormUiState()
            return
        }
        _formulario.value = RecetaFormUiState(id = recetaId, cargando = true)
        viewModelScope.launch {
            _formulario.value = try {
                repository.obtenerReceta(recetaId)?.aFormulario()
                    ?: RecetaFormUiState(id = recetaId, mensajeError = "La receta ya no existe")
            } catch (e: Exception) {
                RecetaFormUiState(id = recetaId, mensajeError = mensajeDeError(e))
            }
        }
    }

    // Una función por campo: guarda lo escrito y borra el error de ese campo,
    // para que el mensaje rojo desaparezca apenas el usuario corrige
    fun onTituloChange(valor: String) =
        _formulario.update { it.copy(titulo = valor, tituloError = null) }

    fun onCategoriaChange(valor: String) =
        _formulario.update { it.copy(categoria = valor, categoriaError = null) }

    // Solo se aceptan dígitos (máximo 4) en el tiempo de preparación
    fun onTiempoPreparacionChange(valor: String) =
        _formulario.update {
            it.copy(
                tiempoPreparacion = valor.filter { c -> c.isDigit() }.take(4),
                tiempoPreparacionError = null
            )
        }

    fun onDescripcionChange(valor: String) =
        _formulario.update { it.copy(descripcion = valor, descripcionError = null) }

    fun onIngredientesChange(valor: String) =
        _formulario.update { it.copy(ingredientes = valor, ingredientesError = null) }

    fun onPreparacionChange(valor: String) =
        _formulario.update { it.copy(preparacion = valor, preparacionError = null) }

    // Revisa todos los campos a la vez y muestra todos los errores juntos.
    // Devuelve true solo si no hay ningún error.
    fun validarFormulario(): Boolean {
        val f = _formulario.value
        val conErrores = f.copy(
            tituloError = validarTituloReceta(f.titulo),
            categoriaError = validarCategoriaReceta(f.categoria),
            tiempoPreparacionError = validarTiempoPreparacion(f.tiempoPreparacion),
            descripcionError = validarDescripcionReceta(f.descripcion),
            ingredientesError = validarTextoLargoReceta(f.ingredientes, "los ingredientes"),
            preparacionError = validarTextoLargoReceta(f.preparacion, "la preparación")
        )
        _formulario.value = conErrores
        return listOf(
            conErrores.tituloError, conErrores.categoriaError, conErrores.tiempoPreparacionError,
            conErrores.descripcionError, conErrores.ingredientesError, conErrores.preparacionError
        ).all { it == null }
    }

    // Guarda la receta: POST si es nueva, PUT si se está editando.
    // onGuardado se ejecuta solo si el servidor respondió bien (la pantalla vuelve atrás).
    // Si falla, el formulario se queda abierto con un mensaje y los datos escritos.
    fun guardarFormulario(onGuardado: () -> Unit) {
        if (!validarFormulario()) return
        val f = _formulario.value
        if (f.guardando) return                      // evita guardar dos veces con doble toque
        _formulario.update { it.copy(guardando = true, mensajeError = null) }

        viewModelScope.launch {
            try {
                if (f.esEdicion) {
                    val actualizada = repository.actualizarReceta(f.aReceta())
                    _mensaje.value =
                        if (actualizada != null) "Receta actualizada" else "La receta ya no existe"
                } else {
                    repository.crearReceta(f.aReceta())
                    _mensaje.value = "Receta creada"
                }
                refrescarLista()
                _formulario.value = RecetaFormUiState()   // queda limpio para la próxima vez
                onGuardado()
            } catch (e: Exception) {
                _formulario.update { it.copy(guardando = false, mensajeError = mensajeDeError(e)) }
            }
        }
    }

    // ===================== ELIMINAR (DELETE) =====================
    fun eliminarReceta(id: Int) {
        viewModelScope.launch {
            try {
                val eliminada = repository.eliminarReceta(id)
                _mensaje.value =
                    if (eliminada) "Receta eliminada" else "La receta ya no existía"
                refrescarLista()
            } catch (e: Exception) {
                _mensaje.value = mensajeDeError(e)
            }
        }
    }

    // La pantalla avisa que ya mostró el mensaje
    fun consumirMensaje() {
        _mensaje.value = null
    }

    // Vuelve a pedir la lista sin mostrar "Cargando", para que la pantalla
    // refleje lo que quedó guardado después de una operación
    private suspend fun refrescarLista() {
        try {
            _estado.value = RecetaUiState.Exito(repository.obtenerRecetas())
        } catch (e: Exception) {
            _estado.value = RecetaUiState.Error(mensajeDeError(e))
        }
    }

    // Traduce una excepción técnica a un mensaje que entiende el usuario
    private fun mensajeDeError(e: Exception): String = when (e) {
        is IOException ->                             // sin red, servidor apagado, tiempo agotado
            "No se pudo conectar con el servidor. Revisa tu conexión e inténtalo de nuevo."
        is HttpException ->                           // el servidor respondió con un código de error
            "El servidor respondió con un error (código ${e.code()})."
        else ->
            "Ocurrió un error inesperado. Inténtalo de nuevo."
    }
}

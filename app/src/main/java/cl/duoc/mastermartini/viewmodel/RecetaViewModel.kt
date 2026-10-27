package cl.duoc.mastermartini.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.database.AppDatabase
import cl.duoc.mastermartini.data.entity.RecetaEntity
import cl.duoc.mastermartini.data.repository.RecetaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecetaViewModel(application: Application) : AndroidViewModel(application) {

    private val recetaDao = AppDatabase
        .getDatabase(application)
        .recetaDao()

    private val repository = RecetaRepository(recetaDao)

    init {
        cargarRecetasIniciales()
    }

    val recetas: StateFlow<List<RecetaEntity>> =
        repository.obtenerTodas().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun cargarRecetasIniciales() {
        viewModelScope.launch {
            val recetasIniciales = listOf(
                RecetaEntity(
                    titulo = "Brownie de chocolate",
                    descripcion = "Brownie húmedo con intenso sabor a chocolate.",
                    categoria = "Pastelería",
                    tiempoPreparacion = 45,
                    ingredientes = "Chocolate, mantequilla, azúcar, huevos y harina.",
                    preparacion = "Derretir el chocolate con la mantequilla. Mezclar con el azúcar y los huevos, incorporar la harina y hornear."
                ),
                RecetaEntity(
                    titulo = "Galletas con chips de chocolate",
                    descripcion = "Galletas caseras crujientes por fuera y suaves por dentro.",
                    categoria = "Pastelería",
                    tiempoPreparacion = 30,
                    ingredientes = "Harina, mantequilla, azúcar, huevo y chips de chocolate.",
                    preparacion = "Mezclar los ingredientes, agregar los chips, formar las galletas y hornear hasta dorar."
                ),
                RecetaEntity(
                    titulo = "Trufas de chocolate",
                    descripcion = "Pequeñas trufas ideales para compartir o vender.",
                    categoria = "Chocolatería",
                    tiempoPreparacion = 25,
                    ingredientes = "Chocolate, crema de leche y cacao en polvo.",
                    preparacion = "Mezclar el chocolate con la crema caliente, refrigerar, formar bolitas y cubrir con cacao."
                ),
                RecetaEntity(
                    titulo = "Pan de molde casero",
                    descripcion = "Pan suave para preparar sándwiches.",
                    categoria = "Panadería",
                    tiempoPreparacion = 120,
                    ingredientes = "Harina, agua, levadura, sal, azúcar y aceite.",
                    preparacion = "Mezclar, amasar, dejar fermentar, colocar en un molde y hornear hasta dorar."
                ),
                RecetaEntity(
                    titulo = "Helado de chocolate",
                    descripcion = "Helado cremoso con sabor a chocolate.",
                    categoria = "Heladería",
                    tiempoPreparacion = 30,
                    ingredientes = "Leche, crema, azúcar y chocolate.",
                    preparacion = "Mezclar la leche con el azúcar y el chocolate, enfriar, incorporar la crema y congelar."
                )
            )

            recetasIniciales.forEach { receta ->
                val cantidad = repository.contarPorTitulo(receta.titulo)

                if (cantidad == 0) {
                    repository.insertar(receta)
                }
            }
        }
    }

    fun insertar(receta: RecetaEntity) {
        viewModelScope.launch {
            repository.insertar(receta)
        }
    }

    fun eliminar(receta: RecetaEntity) {
        viewModelScope.launch {
            repository.eliminar(receta)
        }
    }

}

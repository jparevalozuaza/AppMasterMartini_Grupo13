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

    val recetas: StateFlow<List<RecetaEntity>> =
        repository.obtenerTodas().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

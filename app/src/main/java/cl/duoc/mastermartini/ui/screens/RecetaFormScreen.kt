package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.ui.components.MartiniTextField
import cl.duoc.mastermartini.ui.components.PrimaryButton
import cl.duoc.mastermartini.ui.components.SecondaryButton
import cl.duoc.mastermartini.viewmodel.RecetaViewModel

// ─────────────────────────────────────────────────────────────
// FORMULARIO DE RECETAS: la misma pantalla sirve para crear y para editar.
//   recetaId = 0 → "Nueva receta"  (al guardar: POST)
//   recetaId > 0 → "Editar receta" (al guardar: PUT)
// La pantalla no valida ni guarda nada: solo muestra el estado del ViewModel
// y le avisa cada cambio (onTituloChange, etc.) y el clic en Guardar.
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetaFormScreen(
    recetaId: Int,
    viewModel: RecetaViewModel,
    onVolver: () -> Unit
) {
    val formulario by viewModel.formulario.collectAsState()

    // Al abrir la pantalla se deja el formulario vacío o se carga la receta a editar.
    // "preparado" sobrevive a girar el teléfono (rememberSaveable): así, al girar,
    // no se borra lo que el usuario ya escribió.
    var preparado by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(recetaId) {
        if (!preparado) {
            viewModel.prepararFormulario(recetaId)
            preparado = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recetaId == 0) "Nueva receta" else "Editar receta") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver sin guardar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        // Mientras se trae la receta a editar, se muestra un indicador
        if (formulario.cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            MartiniTextField(
                valor = formulario.titulo,
                onValorChange = viewModel::onTituloChange,
                etiqueta = "Título",
                error = formulario.tituloError
            )

            MartiniTextField(
                valor = formulario.categoria,
                onValorChange = viewModel::onCategoriaChange,
                etiqueta = "Categoría (ej.: Pastelería)",
                error = formulario.categoriaError
            )

            MartiniTextField(
                valor = formulario.tiempoPreparacion,
                onValorChange = viewModel::onTiempoPreparacionChange,
                etiqueta = "Tiempo de preparación (minutos)",
                error = formulario.tiempoPreparacionError,
                tipoTeclado = KeyboardType.Number       // abre el teclado numérico
            )

            MartiniTextField(
                valor = formulario.descripcion,
                onValorChange = viewModel::onDescripcionChange,
                etiqueta = "Descripción breve",
                error = formulario.descripcionError,
                unaLinea = false,
                lineasMinimas = 2,
                imeAction = ImeAction.Default            // Enter = salto de línea
            )

            MartiniTextField(
                valor = formulario.ingredientes,
                onValorChange = viewModel::onIngredientesChange,
                etiqueta = "Ingredientes",
                error = formulario.ingredientesError,
                unaLinea = false,
                lineasMinimas = 3,
                imeAction = ImeAction.Default
            )

            MartiniTextField(
                valor = formulario.preparacion,
                onValorChange = viewModel::onPreparacionChange,
                etiqueta = "Preparación",
                error = formulario.preparacionError,
                unaLinea = false,
                lineasMinimas = 4,
                imeAction = ImeAction.Default
            )

            // Error general (por ejemplo, el servidor no respondió al guardar)
            formulario.mensajeError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Si todo es válido y el servidor responde bien, el ViewModel llama a onVolver
            PrimaryButton(
                texto = if (formulario.esEdicion) "Guardar cambios" else "Crear receta",
                onClick = { viewModel.guardarFormulario(onGuardado = onVolver) },
                cargando = formulario.guardando
            )

            SecondaryButton(
                texto = "Cancelar",
                onClick = onVolver
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

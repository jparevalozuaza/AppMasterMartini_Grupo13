package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.duoc.mastermartini.ui.utils.obtenerWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import cl.duoc.mastermartini.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {

    val windowSizeClass = obtenerWindowSizeClass()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Escuela Martini",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            when (windowSizeClass.widthSizeClass) {

                WindowWidthSizeClass.Compact -> HomeScreenCompacta(viewModel)

                WindowWidthSizeClass.Medium -> HomeScreenMediana(viewModel = null)

                WindowWidthSizeClass.Expanded -> HomeScreenExpandida(viewModel)            }
        }
    }
}

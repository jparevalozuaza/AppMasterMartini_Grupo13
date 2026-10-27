package cl.duoc.mastermartini.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de colores de Material 3 construido con la paleta de marca.
// "on..." = color del texto/ícono que va ENCIMA de ese color (para buen contraste).
private val MartiniColorScheme = lightColorScheme(
    primary = VerdePrincipal,
    onPrimary = Color.White,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeOscuro,
    secondary = Dorado,
    onSecondary = Tinta,
    secondaryContainer = DoradoClaro,
    onSecondaryContainer = Tinta,
    tertiary = VerdeOscuro,
    onTertiary = Color.White,
    background = Crema,
    onBackground = Tinta,
    surface = Crema,
    onSurface = Tinta,
    surfaceVariant = Color.White,
    onSurfaceVariant = GrisTexto,
    outline = Borde,
    outlineVariant = Borde,
    error = ErrorMartini,
    onError = Color.White
)

// Tema de la app. Se conservan los parámetros originales para no romper
// las llamadas existentes, pero siempre se usan los colores de marca
// (sin "colores dinámicos" del fondo de pantalla del celular).
@Composable
fun AppMasterMartini_Grupo13Theme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MartiniColorScheme,
        typography = Typography,
        content = content
    )
}

package it.unibo.noteforall.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    /*primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80*/
    primary = Teal800,
    primaryContainer = Teal800,
    onPrimary = Color.White,
    onPrimaryContainer = Color.White,
    secondaryContainer = Teal100,
    onSecondaryContainer = Color.Black,
    surface = Teal50,
    onSurface = Color.Black,
    errorContainer = Red800,
    onErrorContainer = Color.White,
    outline = Teal800
)

private val LightColorScheme = lightColorScheme(
    /*primary = Teal800,
    secondary = Teal200,
    background = Teal200,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black
*/

    primary = Teal800,
    primaryContainer = Teal800,
    onPrimary = Color.White,
    onPrimaryContainer = Color.White,
    secondaryContainer = Teal100,
    onSecondaryContainer = Color.Black,
    surface = Teal50,
    onSurface = Color.Black,
    errorContainer = Red800,
    onErrorContainer = Color.White,
    outline = Teal800

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun NoteForAllTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
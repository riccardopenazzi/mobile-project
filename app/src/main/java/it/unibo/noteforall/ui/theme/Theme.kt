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
    primary = Teal800,
    secondary = Teal100,
    primaryContainer = Teal800,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onPrimaryContainer = Color.White,
    secondaryContainer = Teal100,
    onSecondaryContainer = Color.Black,
    tertiaryContainer = Gray800,
    onTertiaryContainer = Color.White,
    onSurface = Color.White,
    errorContainer = Red800,
    onErrorContainer = Color.White,
    outline = Teal100,
    onBackground = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Teal800,
    secondary = Teal800,
    primaryContainer = Teal800,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onPrimaryContainer = Color.White,
    secondaryContainer = Teal100,
    onSecondaryContainer = Color.Black,
    tertiaryContainer = LightGray,
    onTertiaryContainer = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    errorContainer = Red800,
    onErrorContainer = Color.White,
    outline = Teal800,
    background = Teal50,
    onBackground = Color.Black
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

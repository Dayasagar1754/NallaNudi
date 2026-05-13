package com.nallanudi.app.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Saffron = Color(0xFFD4550A)
val SaffronDark = Color(0xFFA33D06)
val KannadaBlue = Color(0xFF1565C0)
val ScienceGreen = Color(0xFF2E7D32)
val MathBlue = Color(0xFF1565C0)
val CommerceViolet = Color(0xFF6A1B9A)
val BackgroundCream = Color(0xFFF5F0EB)
val CardWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF666666)

private val LightColorScheme = lightColorScheme(
    primary = Saffron,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCC),
    onPrimaryContainer = Color(0xFF3B0E00),
    secondary = KannadaBlue,
    onSecondary = Color.White,
    background = BackgroundCream,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF0EBE3),
    onSurfaceVariant = TextSecondary,
)

@Composable
fun NallaNudiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}
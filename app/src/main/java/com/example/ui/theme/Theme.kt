package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = TextWhite,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = Color(0xFFC7D2FE),

    secondary = CyanAccent,
    onSecondary = Color(0xFF00363A),
    secondaryContainer = CyanContainer,
    onSecondaryContainer = Color(0xFFA5F3FC),

    tertiary = EmeraldCorrect,
    onTertiary = Color(0xFF003920),
    tertiaryContainer = EmeraldContainer,
    onTertiaryContainer = Color(0xFFA7F3D0),

    error = CrimsonWrong,
    onError = TextWhite,
    errorContainer = CrimsonContainer,
    onErrorContainer = Color(0xFFFECACA),

    background = AmoledBlack,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = DarkCardBorder,
    outlineVariant = Color(0xFF334155)
)

@Composable
fun RipOmrTheme(
    darkTheme: Boolean = true, // Default to true for rich AMOLED experience as requested
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

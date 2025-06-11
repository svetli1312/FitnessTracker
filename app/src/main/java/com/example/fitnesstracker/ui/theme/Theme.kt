package com.example.fitnesstracker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = AccentOrange,
    background = BackgroundLight,
    surface = Color.White,
    error = ErrorRed,
    onPrimary = TextLight,
    onSecondary = Color.Black,
    onBackground = TextDark,
    onSurface = TextDark,
    onError = TextLight,
)

@Composable
fun FitnessTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}

package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDarkColor,
    secondary = SecondaryDarkColor,
    tertiary = TertiaryDarkColor,
    background = CosmicSlateDark,
    surface = ObsidianSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primaryContainer = GoldAccent,
    secondaryContainer = ObsidianSurface,
    error = WasteRed,
    errorContainer = WasteRed
)

@Composable
fun DOGEWatchdogTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

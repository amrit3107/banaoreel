package com.banaoreel.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = ReelRose,
    onPrimary = Color.White,
    primaryContainer = SurfaceMuted,
    onPrimaryContainer = ReelRoseDark,
    secondary = ReelViolet,
    onSecondary = Color.White,
    secondaryContainer = SurfaceMuted,
    onSecondaryContainer = ReelVioletDark,
    tertiary = ReelGold,
    onTertiary = Ink,
    background = Paper,
    onBackground = Ink,
    surface = Surface,
    onSurface = Ink,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = InkMuted,
    error = ErrorRed,
    onError = Color.White
)

// Dark theme kept close to the light palette's hue relationships (same rose/
// violet/gold identity) rather than a generic near-black Material default --
// the brand should still read as BanaoReel at night, not as a different app.
private val DarkColors = darkColorScheme(
    primary = ReelRose,
    onPrimary = Color.White,
    primaryContainer = ReelRoseDark,
    onPrimaryContainer = Color.White,
    secondary = ReelViolet,
    onSecondary = Color.White,
    secondaryContainer = ReelVioletDark,
    onSecondaryContainer = Color.White,
    tertiary = ReelGold,
    onTertiary = Ink,
    background = Color(0xFF15111C),
    onBackground = Color(0xFFF3EEE6),
    surface = Color(0xFF1E1828),
    onSurface = Color(0xFFF3EEE6),
    surfaceVariant = Color(0xFF2A2236),
    onSurfaceVariant = Color(0xFFC9C0D6),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun BanaoReelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = BanaoReelTypography,
        shapes = BanaoReelShapes,
        content = content
    )
}

package com.honari.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = CardWhite,
    primaryContainer = PrimaryTealContainer,
    onPrimaryContainer = PrimaryTealDark,
    secondary = AccentCoral,
    onSecondary = CardWhite,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF7C2900),
    tertiary = AccentIndigo,
    onTertiary = CardWhite,
    tertiaryContainer = Color(0xFFE8EAF6),
    onTertiaryContainer = Color(0xFF1A237E),
    background = BackgroundBeige,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceBeige,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = CardWhite,
    outline = BrownLight,
    outlineVariant = Color(0xFFD7CCC8),
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTealLight,
    onPrimary = Color(0xFF1A3A45),
    primaryContainer = PrimaryTealDark,
    onPrimaryContainer = PrimaryTealContainer,
    secondary = BrownLight,
    onSecondary = Color(0xFF2A1A08),
    secondaryContainer = Color(0xFF3A2510),
    onSecondaryContainer = BrownContainer,
    background = Color(0xFF1A1815),
    onBackground = Color(0xFFE8E4DC),
    surface = Color(0xFF252220),
    onSurface = Color(0xFFE8E4DC),
    surfaceVariant = Color(0xFF302D2A),
    onSurfaceVariant = Color(0xFFB0A898),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF3D0000),
    outline = Color(0xFF706050),
    outlineVariant = Color(0xFF4A403A),
)

@Composable
fun HonariTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HonariTypography,
        content = content,
    )
}

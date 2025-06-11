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

/** Light color scheme – vibrant violet/coral palette. */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,

    secondary = SecondaryColor,
    onSecondary = Color.White,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = SecondaryDark,

    tertiary = TertiaryColor,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB2F5E8),
    onTertiaryContainer = Color(0xFF00372C),

    background = BackgroundColor,
    onBackground = PrimaryTextColor,

    surface = SurfaceColor,
    onSurface = PrimaryTextColor,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = SecondaryTextColor,

    error = ErrorColor,
    onError = Color.White,

    outline = SecondaryTextColor,
    outlineVariant = TertiaryTextColor
)

/** Dark color scheme – deep navy/violet palette with vibrant accent colors. */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = Color(0xFF21005E),
    primaryContainer = PrimaryColor,
    onPrimaryContainer = Color(0xFFE9DDFF),

    secondary = SecondaryLight,
    onSecondary = Color(0xFF5C0000),
    secondaryContainer = SecondaryColor,
    onSecondaryContainer = Color(0xFFFFDAD6),

    tertiary = TertiaryColor,
    onTertiary = Color(0xFF003728),
    tertiaryContainer = Color(0xFF005142),
    onTertiaryContainer = Color(0xFFB2F5E8),

    background = DarkBackgroundColor,
    onBackground = Color(0xFFEAE0FF),

    surface = DarkSurfaceColor,
    onSurface = Color(0xFFEAE0FF),
    surfaceVariant = DarkSurfaceVariantColor,
    onSurfaceVariant = Color(0xFFCAB8ED),

    error = Color(0xFFFF897A),
    onError = Color(0xFF5F0000),

    outline = Color(0xFF9880C8),
    outlineVariant = Color(0xFF4A3870)
)

/**
 * Main theme composable for Honari app.
 * Supports light & dark mode; dynamic color is disabled so our vibrant palette is always used.
 */
@Composable
fun HonariTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HonariTypography,
        content = content
    )
}

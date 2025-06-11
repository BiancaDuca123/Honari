package com.honari.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.honari.app.R

/**
 * Google Fonts provider configuration.
 * Using Google Fonts for better performance and smaller APK size.
 */
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Crimson Text for headings and literary content
val CrimsonTextFont = GoogleFont("Crimson Text")

val CrimsonFont = FontFamily(
    Font(
        googleFont = CrimsonTextFont,
        fontProvider = provider,
        weight = FontWeight.Normal
    ),
    Font(
        googleFont = CrimsonTextFont,
        fontProvider = provider,
        weight = FontWeight.Bold
    ),
    Font(
        googleFont = CrimsonTextFont,
        fontProvider = provider,
        weight = FontWeight.SemiBold
    ),
    Font(
        googleFont = CrimsonTextFont,
        fontProvider = provider,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    )
)

// Inter for body text and UI elements
val InterGoogleFont = GoogleFont("Inter")

val InterFont = FontFamily(
    Font(
        googleFont = InterGoogleFont,
        fontProvider = provider,
        weight = FontWeight.Normal
    ),
    Font(
        googleFont = InterGoogleFont,
        fontProvider = provider,
        weight = FontWeight.Medium
    ),
    Font(
        googleFont = InterGoogleFont,
        fontProvider = provider,
        weight = FontWeight.SemiBold
    ),
    Font(
        googleFont = InterGoogleFont,
        fontProvider = provider,
        weight = FontWeight.Bold
    )
)

/**
 * Custom typography for Honari app.
 * Uses Crimson Text for headers and Inter for body text.
 * All fonts are loaded from Google Fonts.
 */
val HonariTypography = Typography(
    // Display styles
    displayLarge = TextStyle(
        fontFamily = CrimsonFont,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = CrimsonFont,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = CrimsonFont,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),

    // Headline styles
    headlineLarge = TextStyle(
        fontFamily = CrimsonFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = CrimsonFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = CrimsonFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),

    // Title styles
    titleLarge = TextStyle(
        fontFamily = CrimsonFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = CrimsonFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    // Body styles
    bodyLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    // Label styles
    labelLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)
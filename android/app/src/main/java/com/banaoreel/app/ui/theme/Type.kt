package com.banaoreel.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

// Downloadable Google Fonts (resolved on-device via Google Play services) --
// avoids bundling font binaries directly, and stays current with hinting/
// subsetting improvements upstream.
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = com.banaoreel.app.R.array.com_google_android_gms_fonts_certs
)

// Baloo 2: rounded, bold, playful -- carries the brand's personality on
// headlines and the hero Create moment. Deliberately not the Android system
// default (Roboto), which would read as no design decision at all.
private val BalooFamily = FontFamily(
    Font(GoogleFont("Baloo 2"), provider, FontWeight.SemiBold),
    Font(GoogleFont("Baloo 2"), provider, FontWeight.Bold)
)

// Plus Jakarta Sans: clean geometric sans for body/UI text -- warmer than
// Inter/system-default sans, and its numerals stay legible at small sizes
// for ₹ amounts throughout the wallet and pricing screens.
private val JakartaFamily = FontFamily(
    Font(GoogleFont("Plus Jakarta Sans"), provider, FontWeight.Normal),
    Font(GoogleFont("Plus Jakarta Sans"), provider, FontWeight.Medium),
    Font(GoogleFont("Plus Jakarta Sans"), provider, FontWeight.SemiBold),
    Font(GoogleFont("Plus Jakarta Sans"), provider, FontWeight.Bold)
)

val BanaoReelTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = BalooFamily, fontWeight = FontWeight.Bold,
        fontSize = 40.sp, lineHeight = 46.sp, letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = BalooFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp, lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = BalooFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp
    )
)

package com.example.medcards.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8BAAFF),
    onPrimary = Color(0xFF102A5C),
    primaryContainer = Color(0xFF1E3A66),
    onPrimaryContainer = Color(0xFFDCE7FF),
    secondary = Color(0xFF6EE7B7),
    onSecondary = Color(0xFF063B2A),
    tertiary = WarningAmber,
    onTertiary = TextDark,
    error = ErrorRed,
    onError = CardWhite,
    background = Color(0xFF0B1220),
    onBackground = LightColdGrey,
    surface = Color(0xFF111C2E),
    onSurface = LightColdGrey,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFB6C2D2)
)

private val LightColorScheme = lightColorScheme(
    primary = CobaltBlue,
    onPrimary = CardWhite,
    primaryContainer = CobaltBlueContainer,
    onPrimaryContainer = CobaltBlueDark,
    secondary = MintGreen,
    onSecondary = TextDark,
    tertiary = WarningAmber,
    onTertiary = TextDark,
    error = ErrorRed,
    onError = CardWhite,
    background = LightColdGrey,
    onBackground = TextDark,
    surface = CardWhite,
    onSurface = TextDark,
    surfaceVariant = LightColdGrey,
    onSurfaceVariant = TextMuted
)

@Composable
fun MedCardsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
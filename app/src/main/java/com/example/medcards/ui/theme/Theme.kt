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

private val DarkColorScheme = darkColorScheme(
    primary = CobaltBlue,
    onPrimary = CardWhite,
    secondary = MintGreen,
    onSecondary = TextDark,
    tertiary = WarningAmber,
    onTertiary = TextDark,
    error = ErrorRed,
    onError = CardWhite,
    background = TextDark,
    onBackground = LightColdGrey,
    surface = TextDark,
    onSurface = LightColdGrey,
    surfaceVariant = TextDark,
    onSurfaceVariant = TextMuted
)

private val LightColorScheme = lightColorScheme(
    primary = CobaltBlue,
    onPrimary = CardWhite,
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
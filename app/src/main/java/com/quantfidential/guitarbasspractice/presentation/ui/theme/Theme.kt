package com.quantfidential.guitarbasspractice.presentation.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// High contrast, color-blind friendly colors
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Purple20,
    onSecondary = PurpleGrey20,
    onTertiary = Pink20,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    error = ErrorRed,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnPrimary,
    onSecondary = LightOnSecondary,
    onTertiary = LightOnTertiary,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    error = ErrorRed,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

// Color-blind friendly color scheme (alternative)
private val ColorBlindFriendlyDarkScheme = darkColorScheme(
    primary = ColorBlindBlue,
    secondary = ColorBlindOrange,
    tertiary = ColorBlindYellow,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = LightOnPrimary,
    onSecondary = LightOnSecondary,
    onTertiary = DarkOnBackground,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

private val ColorBlindFriendlyLightScheme = lightColorScheme(
    primary = ColorBlindBlueDark,
    secondary = ColorBlindOrangeDark,
    tertiary = ColorBlindYellowDark,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnPrimary,
    onSecondary = LightOnSecondary,
    onTertiary = LightOnTertiary,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface
)

// High contrast scheme for accessibility
private val HighContrastDarkScheme = darkColorScheme(
    primary = HighContrastPrimary,
    secondary = HighContrastSecondary,
    background = HighContrastDarkBg,
    surface = HighContrastDarkSurface,
    onPrimary = HighContrastOnPrimary,
    onSecondary = HighContrastOnSecondary,
    onBackground = HighContrastDarkOnBg,
    onSurface = HighContrastDarkOnSurface
)

private val HighContrastLightScheme = lightColorScheme(
    primary = HighContrastPrimaryLight,
    secondary = HighContrastSecondaryLight,
    background = HighContrastLightBg,
    surface = HighContrastLightSurface,
    onPrimary = HighContrastOnPrimaryLight,
    onSecondary = HighContrastOnSecondaryLight,
    onBackground = HighContrastLightOnBg,
    onSurface = HighContrastLightOnSurface
)

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class ColorSchemeType {
    DEFAULT, COLOR_BLIND_FRIENDLY, HIGH_CONTRAST
}

@Composable
fun GuitarBassPracticeAppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    colorSchemeType: ColorSchemeType = ColorSchemeType.DEFAULT,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        
        colorSchemeType == ColorSchemeType.COLOR_BLIND_FRIENDLY -> {
            if (darkTheme) ColorBlindFriendlyDarkScheme else ColorBlindFriendlyLightScheme
        }
        
        colorSchemeType == ColorSchemeType.HIGH_CONTRAST -> {
            if (darkTheme) HighContrastDarkScheme else HighContrastLightScheme
        }
        
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.hlasoftware.focus.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MidnightGreenLight, // For titles, selected icons
    onPrimary = Night,
    primaryContainer = MidnightGreen, // For the date selector bar
    onPrimaryContainer = Color.White,
    secondary = MidnightGreenLight,
    onSecondary = Night,
    tertiary = OrangeAccent, // FAB color
    onTertiary = Night, // Icon color on FAB
    background = Night, // Main screen background
    onBackground = Color.White,
    surface = EerieBlack, // Bottom navigation background
    onSurface = Gray, // Unselected icons/text in nav bar
    surfaceVariant = Jet, // Activity card background
    onSurfaceVariant = Color.White, // Text on activity cards
    outline = Gray
)

// Placeholder for light theme - you can customize this later
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun FocusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to enforce custom theme
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

package com.example.libraryapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Colors sampled from the real app's screenshots: teal app bar,
// blue FAB/accents, light grey-blue background.
val MementoTeal = Color(0xFF26A69A)
val MementoTealDark = Color(0xFF1D8B80)
val MementoBlue = Color(0xFF1E88E5)
val MementoBackground = Color(0xFFF5F5F7)
val MementoSurface = Color(0xFFFFFFFF)
val MementoOnTeal = Color(0xFFFFFFFF)
val MementoCardBlue = Color(0xFF2E9BE0)
val MementoAccentYellow = Color(0xFFFFC940)

private val MementoColorScheme = lightColorScheme(
    primary = MementoTeal,
    onPrimary = MementoOnTeal,
    primaryContainer = MementoTealDark,
    secondary = MementoBlue,
    onSecondary = Color.White,
    background = MementoBackground,
    surface = MementoSurface,
    surfaceVariant = Color(0xFFECECEC),
    onSurfaceVariant = Color(0xFF6E6E6E)
)

private val MementoTypography = Typography(
    titleLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)
)

@Composable
fun LibraryAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MementoColorScheme,
        typography = MementoTypography,
        content = content
    )
}

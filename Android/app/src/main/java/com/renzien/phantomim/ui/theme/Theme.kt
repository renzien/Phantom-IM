package com.renzien.phantomim.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PhantomColorScheme = darkColorScheme(
    primary = PhantomWhite,
    onPrimary = PhantomBlack,
    primaryContainer = PhantomBlack,
    onPrimaryContainer = PhantomWhite,

    // Secondary Color
    secondary = PhantomYellow,
    onSecondary = PhantomBlack,

    // Third Color
    tertiary = PhantomPink,
    onTertiary = PhantomBlack,

    // Background
    background = PhantomRed,
    onBackground = PhantomWhite,

    // Surface
    surface = PhantomBlack,
    onSurface = PhantomWhite,
    surfaceVariant = PhantomBlack,
    onSurfaceVariant = PhantomWhite,

    outline = PhantomWhite
)

@Composable
fun PhantomIMTheme(
    content: @Composable () -> Unit
){
    MaterialTheme(
        colorScheme = PhantomColorScheme,
        typography = Typography,
        content = content
    )
}
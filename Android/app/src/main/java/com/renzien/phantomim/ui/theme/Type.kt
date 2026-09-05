package com.renzien.phantomim.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.renzien.phantomim.R

val PhantomFontFamily = FontFamily(
    Font(
        resId = R.font.optima_nova_black,
        weight = FontWeight.Black
    )
)

private val DefaultTypography = Typography()

private fun TextStyle.withPhantomFont(): TextStyle {
    return copy(
        fontFamily = PhantomFontFamily,
        fontWeight = FontWeight.Black
    )
}

val Typography = Typography(
    displayLarge = DefaultTypography.displayLarge.withPhantomFont(),
    displayMedium = DefaultTypography.displayMedium.withPhantomFont(),
    displaySmall = DefaultTypography.displaySmall.withPhantomFont(),

    headlineLarge = DefaultTypography.headlineLarge.withPhantomFont(),
    headlineMedium = DefaultTypography.headlineMedium.withPhantomFont(),
    headlineSmall = DefaultTypography.headlineSmall.withPhantomFont(),

    titleLarge = DefaultTypography.titleLarge.withPhantomFont(),
    titleMedium = DefaultTypography.titleMedium.withPhantomFont(),
    titleSmall = DefaultTypography.titleSmall.withPhantomFont(),

    bodyLarge = DefaultTypography.bodyLarge.withPhantomFont(),
    bodyMedium = DefaultTypography.bodyMedium.withPhantomFont(),
    bodySmall = DefaultTypography.bodySmall.withPhantomFont(),

    labelLarge = DefaultTypography.labelLarge.withPhantomFont(),
    labelMedium = DefaultTypography.labelMedium.withPhantomFont(),
    labelSmall = DefaultTypography.labelSmall.withPhantomFont()
)
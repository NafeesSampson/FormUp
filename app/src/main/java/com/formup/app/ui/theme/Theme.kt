package com.formup.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FormUpColorScheme = lightColorScheme(
    primary = FormUpColors.Primary,
    onPrimary = FormUpColors.Surface,
    primaryContainer = FormUpColors.Mint,
    onPrimaryContainer = FormUpColors.PrimaryDeep,
    secondary = FormUpColors.PrimaryDeep,
    onSecondary = FormUpColors.Surface,
    background = FormUpColors.Background,
    onBackground = FormUpColors.TextPrimary,
    surface = FormUpColors.Surface,
    onSurface = FormUpColors.TextPrimary,
    surfaceVariant = FormUpColors.PrimaryTint,
    onSurfaceVariant = FormUpColors.TextSecondary,
    error = FormUpColors.Danger,
    outline = FormUpColors.Hairline
)

@Composable
fun FormUpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FormUpColorScheme,
        typography = FormUpTypography,
        content = content
    )
}

package com.formup.app.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.formup.app.ui.theme.FormUpColors

// Simple progress bar with a background track and filled progress indicator
@Composable
fun StatProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    fillColor: Color = FormUpColors.Primary,
    trackColor: Color = FormUpColors.Hairline,
    barHeight: Dp = 6.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50))
                .background(fillColor)
        )
    }
}

// Split comparative progress bar showing proportions between two opposing stats
@Composable
fun DualStatBar(
    leftValue: Float,
    rightValue: Float,
    modifier: Modifier = Modifier,
    leftColor: Color = FormUpColors.Hairline,
    rightColor: Color = FormUpColors.Primary,
    barHeight: Dp = 6.dp
) {
    val safeLeft = leftValue.coerceAtLeast(0f)
    val safeRight = rightValue.coerceAtLeast(0f)
    val hasAnyValue = (safeLeft + safeRight) > 0f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(RoundedCornerShape(50))
    ) {
        Box(
            modifier = Modifier
                .weight(if (hasAnyValue) safeLeft.coerceAtLeast(0.0001f) else 1f)
                .fillMaxHeight()
                .background(leftColor)
        )
        Box(
            modifier = Modifier
                .weight(if (hasAnyValue) safeRight.coerceAtLeast(0.0001f) else 1f)
                .fillMaxHeight()
                .background(rightColor)
        )
    }
}

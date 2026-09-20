package com.formup.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.Mono

/** White rounded panel used for every block on the home feed. */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.foundation.layout.PaddingValues =
        androidx.compose.foundation.layout.PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = FormUpColors.Surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, FormUpColors.Hairline)
    ) {
        Column(Modifier.padding(contentPadding), content = content)
    }
}


@Composable
fun StatusPill(
    text: String,
    background: Color = FormUpColors.Mint,
    contentColor: Color = FormUpColors.PrimaryDeep
) {
    Surface(shape = RoundedCornerShape(50), color = background) {
        Text(
            text = text,
            color = contentColor,
            fontFamily = Mono,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.6.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}


@Composable
fun IconBubble(
    size: androidx.compose.ui.unit.Dp = 40.dp,
    background: Color = FormUpColors.PrimaryTint,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
        content = { content() }
    )
}

/** Legend marker used under the availability bar. */
@Composable
fun LegendDot(color: Color) {
    Box(
        Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(color)
    )
}

package com.formup.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.Availability
import com.formup.app.ui.theme.FormUpColors

@Composable
fun AvailabilityCard(
    availability: Availability,
    onInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    SectionCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Match Availability",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "About availability",
                tint = FormUpColors.TextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .clickable(onClick = onInfo)
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = availability.fit.toString(),
                color = FormUpColors.Primary,
                fontWeight = FontWeight.Bold,
                fontSize = 38.sp
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = "Available Players",
                style = MaterialTheme.typography.bodyMedium,
                color = FormUpColors.TextSecondary
            )
        }

        Spacer(Modifier.height(14.dp))

        AvailabilityBar(availability)

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Legend("Fit", availability.fit, FormUpColors.Primary)
            Legend("Doubt", availability.doubtful, FormUpColors.Amber)
            Legend("Out", availability.out, FormUpColors.Danger)
        }
    }
}

@Composable
private fun AvailabilityBar(availability: Availability) {
    val total = availability.total.coerceAtLeast(1)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(FormUpColors.Hairline)
    ) {
        Segment(availability.fit.toFloat() / total, FormUpColors.Primary)
        Segment(availability.doubtful.toFloat() / total, FormUpColors.Amber)
        Segment(availability.out.toFloat() / total, FormUpColors.Danger)
    }
}

@Composable
private fun RowScope.Segment(fraction: Float, color: Color) {
    if (fraction <= 0f) return
    Box(
        Modifier
            .weight(fraction)
            .fillMaxHeight()
            .background(color)
    )
}

@Composable
private fun Legend(label: String, count: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        LegendDot(color)
        Spacer(Modifier.size(6.dp))
        Text(
            text = "$label ($count)",
            style = MaterialTheme.typography.labelMedium,
            color = FormUpColors.TextSecondary
        )
    }
}

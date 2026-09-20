package com.formup.app.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.formup.app.ui.theme.FormUpColors

@Composable
fun QuickActionsRow(
    onAttendance: () -> Unit,
    onSelection: () -> Unit,
    onStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickAction("Attendance", Icons.Filled.HowToReg, onAttendance)
        QuickAction("Selection", Icons.Filled.Groups, onSelection)
        QuickAction("Stats", Icons.Filled.BarChart, onStats)
    }
}

@Composable
private fun RowScope.QuickAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        color = FormUpColors.Surface,
        border = BorderStroke(1.dp, FormUpColors.Hairline)
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconBubble {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FormUpColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = FormUpColors.Primary
            )
        }
    }
}

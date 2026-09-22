package com.formup.app.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.formup.app.ui.home.TeamUpdate
import com.formup.app.ui.home.UpdateTone
import com.formup.app.ui.theme.FormUpColors

@Composable
fun UpdateCard(update: TeamUpdate, modifier: Modifier = Modifier) {
    val bubbleColor = when (update.tone) {
        UpdateTone.Notice -> FormUpColors.AmberTint
        UpdateTone.Medical -> FormUpColors.PrimaryTint
    }
    val iconColor = when (update.tone) {
        UpdateTone.Notice -> FormUpColors.AmberIcon
        UpdateTone.Medical -> FormUpColors.Primary
    }

    SectionCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            IconBubble(size = 34.dp, background = bubbleColor) {
                Icon(
                    imageVector = update.icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = update.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = FormUpColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = update.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = FormUpColors.TextSecondary
                    )
                }
                Spacer(Modifier.height(5.dp))
                Text(
                    text = update.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = FormUpColors.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

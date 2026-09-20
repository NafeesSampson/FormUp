package com.formup.app.ui.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.IconBubble
import com.formup.app.ui.notifications.NotificationItem
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.Mono

/**
 * A single row in the Notifications feed. Unread ("new") items carry a solid
 * accent bar on the leading edge; read items render flush.
 */
@Composable
fun NotificationRow(
    item: NotificationItem,
    isUnread: Boolean,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Box(
            Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(if (isUnread) FormUpColors.Primary else Color.Transparent)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, top = 14.dp, bottom = 14.dp, end = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                IconBubble(size = 36.dp, background = item.iconBackground) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = FormUpColors.TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = item.timestamp,
                            fontFamily = Mono,
                            fontSize = 11.sp,
                            color = FormUpColors.TextSecondary
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.body,
                        style = MaterialTheme.typography.bodySmall,
                        color = FormUpColors.TextSecondary
                    )

                    if (item.actionLabel != null) {
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FormUpColors.Primary,
                            modifier = Modifier.clickable(onClick = onAction)
                        ) {
                            Text(
                                text = item.actionLabel,
                                fontFamily = Mono,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = FormUpColors.Surface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

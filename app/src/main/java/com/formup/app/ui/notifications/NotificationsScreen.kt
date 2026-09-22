package com.formup.app.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.FormUpBottomBar
import com.formup.app.ui.home.components.FormUpTopBar
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.notifications.components.NotificationRow
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

@Composable
fun NotificationsScreen(
    state: NotificationsUiState = SampleNotificationsState,
    unreadIds: Set<String> = emptySet(),
    onBack: () -> Unit = {},
    onMarkAllRead: () -> Unit = {},
    onOpenNotification: (String) -> Unit = {},
    onSelectTab: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val grouped = state.grouped

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            FormUpTopBar(
                hasUnread = false,
                onNotifications = onBack
            )
        },
        bottomBar = {
            FormUpBottomBar(
                selected = HomeTab.Home,
                onSelect = onSelectTab
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.headlineMedium,
                    color = FormUpColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Mark all as read",
                    fontFamily = Mono,
                    fontSize = 11.sp,
                    color = FormUpColors.Primary,
                    modifier = Modifier.clickable(onClick = onMarkAllRead)
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                color = FormUpColors.Surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, FormUpColors.Hairline)
            ) {
                val orderedGroups = NotificationGroup.entries.mapNotNull { group ->
                    grouped[group]?.takeIf { it.isNotEmpty() }?.let { group to it }
                }
                val lastItemId = orderedGroups.lastOrNull()?.second?.lastOrNull()?.id

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    orderedGroups.forEach { (group, groupItems) ->
                        item(key = "${group.name}-header") {
                            Text(
                                text = group.label,
                                fontFamily = Mono,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                letterSpacing = 0.6.sp,
                                color = FormUpColors.TextSecondary,
                                modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 4.dp)
                            )
                        }

                        items(groupItems, key = { it.id }) { notification ->
                            NotificationRow(
                                item = notification,
                                isUnread = notification.id in unreadIds,
                                onAction = { onOpenNotification(notification.id) }
                            )
                            if (notification.id != lastItemId) {
                                HorizontalDivider(color = FormUpColors.Hairline)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun NotificationsScreenPreview() {
    FormUpTheme {
        NotificationsScreen()
    }
}
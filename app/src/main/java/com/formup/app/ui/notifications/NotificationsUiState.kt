package com.formup.app.ui.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.formup.app.ui.theme.FormUpColors

// Feed section categories for grouping notifications
enum class NotificationGroup(val label: String) {
    New("NEW"),
    Earlier("EARLIER")
}

// Data model for an individual notification item
data class NotificationItem(
    val id: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color,
    val title: String,
    val timestamp: String,
    val body: String,
    val group: NotificationGroup,
    val actionLabel: String? = null // Optional button text (e.g. "RSVP Now")
)

// UI state holding the notification feed
data class NotificationsUiState(
    val items: List<NotificationItem>
) {
    val grouped: Map<NotificationGroup, List<NotificationItem>>
        get() = items.groupBy { it.group }
}

// Mock notification data for previews and testing
val SampleNotificationsState = NotificationsUiState(
    items = listOf(
        NotificationItem(
            id = "attendance-nudge",
            icon = Icons.Filled.NotificationsActive,
            iconBackground = FormUpColors.PrimaryTint,
            iconTint = FormUpColors.Primary,
            title = "Attendance Nudge",
            timestamp = "2m ago",
            body = "Coach Sarah sent a nudge for Thursday's Tactical Training.",
            group = NotificationGroup.New,
            actionLabel = "RSVP Now"
        ),
        NotificationItem(
            id = "team-update",
            icon = Icons.Filled.CalendarMonth,
            iconBackground = FormUpColors.AmberTint,
            iconTint = FormUpColors.AmberIcon,
            title = "Team Update",
            timestamp = "1h ago",
            body = "Match Location Changed: Saturday's match vs. Metro United is now at Field 4.",
            group = NotificationGroup.New
        ),
        NotificationItem(
            id = "fitness-alert",
            icon = Icons.Filled.MonitorHeart,
            iconBackground = FormUpColors.PrimaryTint,
            iconTint = FormUpColors.TextSecondary,
            title = "Fitness Alert",
            timestamp = "Yesterday",
            body = "Injury Update: Sam Wilson is cleared for full activity.",
            group = NotificationGroup.Earlier
        ),
        NotificationItem(
            id = "stats-update",
            icon = Icons.Filled.QueryStats,
            iconBackground = FormUpColors.PrimaryTint,
            iconTint = FormUpColors.TextSecondary,
            title = "Stats Update",
            timestamp = "Yesterday",
            body = "New Match Report: Review stats from yesterday's win vs Rovers FC.",
            group = NotificationGroup.Earlier
        )
    )
)

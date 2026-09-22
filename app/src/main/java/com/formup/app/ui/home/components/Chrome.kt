package com.formup.app.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.R
import com.formup.app.ui.theme.FormUpColors

@Composable
fun FormUpTopBar(
    hasUnread: Boolean,
    onNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.background(FormUpColors.Background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "FormUp Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.size(10.dp))
            Text(
                text = "FormUp",
                color = FormUpColors.Primary,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
            Spacer(Modifier.weight(1f))
            Box {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = FormUpColors.TextPrimary,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(onClick = onNotifications)
                )
                if (hasUnread) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 1.dp, y = (-1).dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(FormUpColors.Danger)
                    )
                }
            }
        }
        HorizontalDivider(color = FormUpColors.Hairline)
    }
}

enum class HomeTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Calendar("Calendar", Icons.Filled.CalendarMonth),
    Team("Team", Icons.Filled.Groups),
    Stats("Stats", Icons.Filled.BarChart),
    Profile("Profile", Icons.Filled.Person)
}

@Composable
fun FormUpBottomBar(
    selected: HomeTab,
    onSelect: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        HorizontalDivider(color = FormUpColors.Hairline)
        NavigationBar(
            containerColor = FormUpColors.Background,
            tonalElevation = 0.dp
        ) {
            HomeTab.entries.forEach { tab ->
                NavigationBarItem(
                    selected = tab == selected,
                    onClick = { onSelect(tab) },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(21.dp)
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = FormUpColors.PrimaryDeep,
                        selectedTextColor = FormUpColors.PrimaryDeep,
                        unselectedIconColor = FormUpColors.TextSecondary,
                        unselectedTextColor = FormUpColors.TextSecondary,
                        indicatorColor = FormUpColors.NavIndicator
                    )
                )
            }
        }
    }
}
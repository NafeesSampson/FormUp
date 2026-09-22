package com.formup.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.FormUpBottomBar
import com.formup.app.ui.home.components.FormUpTopBar
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.home.components.IconBubble
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.home.components.StatusPill
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

// Main profile and settings screen
@Composable
fun ProfileScreen(
    state: ProfileUiState = SampleProfileState,
    onNotifications: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onEditTeamInfo: () -> Unit = {},
    onLanguageSelected: (LanguageOption) -> Unit = {},
    onSignOut: () -> Unit = {},
    onSelectTab: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var language by remember { mutableStateOf(state.language) }
    var matchReminders by remember { mutableStateOf(state.matchReminders) }
    var weeklySummary by remember { mutableStateOf(state.weeklySummary) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            FormUpTopBar(
                hasUnread = state.unreadNotifications,
                onNotifications = onNotifications
            )
        },
        bottomBar = {
            FormUpBottomBar(selected = HomeTab.Profile, onSelect = onSelectTab)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Coach profile header
            item {
                ProfileHeaderCard(
                    name = state.name,
                    role = state.role,
                    badges = state.badges,
                    onEditProfile = onEditProfile
                )
            }

            // Season summary tiles (2-column grid layout)
            item { SectionHeading("Season Stats") }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    state.seasonStats.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { stat ->
                                StatTile(stat = stat, modifier = Modifier.weight(1f))
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            // Recent activity log
            item { SectionHeading("Recent Activity") }

            item {
                SectionCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    state.recentActivity.forEachIndexed { index, activity ->
                        ActivityRow(activity)
                        if (index != state.recentActivity.lastIndex) {
                            HorizontalDivider(
                                color = FormUpColors.Hairline,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )
                        }
                    }
                }
            }

            // App settings and notifications preferences
            item { SectionHeading("Preferences") }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth()) {
                    LanguageSelector(
                        selected = language,
                        onSelected = {
                            language = it
                            onLanguageSelected(it)
                        }
                    )
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(color = FormUpColors.Hairline)
                    Spacer(Modifier.height(4.dp))
                    ToggleRow(
                        title = "Match Reminders",
                        subtitle = "Push alerts 24h before kickoff",
                        checked = matchReminders,
                        onCheckedChange = { matchReminders = it }
                    )
                    ToggleRow(
                        title = "Weekly Summary",
                        subtitle = "Squad performance digest every Monday",
                        checked = weeklySummary,
                        onCheckedChange = { weeklySummary = it }
                    )
                }
            }

            // Team metadata card
            item { SectionHeading("Team Information") }

            item {
                TeamInfoCard(info = state.teamInfo, onEdit = onEditTeamInfo)
            }

            // Sign out action
            item {
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, FormUpColors.Hairline)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = FormUpColors.Danger,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "Sign Out",
                        color = FormUpColors.Danger,
                        fontFamily = Mono,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = FormUpColors.TextPrimary,
        modifier = Modifier.padding(top = 6.dp)
    )
}

// Coach avatar, title, role badges, and edit button
@Composable
private fun ProfileHeaderCard(
    name: String,
    role: String,
    badges: List<String>,
    onEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = FormUpColors.Surface,
        border = BorderStroke(1.dp, FormUpColors.Hairline)
    ) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(FormUpColors.PrimaryDeep)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .border(2.dp, FormUpColors.Primary, CircleShape)
                        .background(FormUpColors.Surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile photo",
                        tint = FormUpColors.TextPrimary,
                        modifier = Modifier.size(58.dp)
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = FormUpColors.TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = role.uppercase(),
                    fontFamily = Mono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = FormUpColors.TextSecondary
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    badges.forEach { badge ->
                        StatusPill(
                            text = badge,
                            background = FormUpColors.PrimaryTint,
                            contentColor = FormUpColors.TextSecondary
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onEditProfile,
                    modifier = Modifier.height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, FormUpColors.Hairline)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = FormUpColors.TextPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "Edit Profile",
                        color = FormUpColors.TextPrimary,
                        fontFamily = Mono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Single metric card used in season stats grid
@Composable
private fun StatTile(stat: SeasonStat, modifier: Modifier = Modifier) {
    SectionCard(modifier = modifier, contentPadding = PaddingValues(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stat.label.uppercase(),
                fontFamily = Mono,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                letterSpacing = 0.6.sp,
                color = FormUpColors.TextSecondary
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = stat.icon,
                contentDescription = null,
                tint = FormUpColors.TextSecondary,
                modifier = Modifier.size(15.dp)
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = stat.value,
            style = MaterialTheme.typography.headlineMedium,
            color = FormUpColors.TextPrimary
        )
    }
}

// Item row showing recent match/training event activity
@Composable
private fun ActivityRow(activity: ActivityItem) {
    val bubbleColor = when (activity.tone) {
        ActivityTone.Match -> FormUpColors.PrimaryTint
        ActivityTone.Training -> FormUpColors.AmberTint
    }
    val iconColor = when (activity.tone) {
        ActivityTone.Match -> FormUpColors.Primary
        ActivityTone.Training -> FormUpColors.AmberIcon
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBubble(size = 38.dp, background = bubbleColor) {
            Icon(
                imageVector = activity.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium,
                color = FormUpColors.TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = activity.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = FormUpColors.TextSecondary
            )
        }
        Spacer(Modifier.size(8.dp))
        Text(
            text = activity.date,
            fontFamily = Mono,
            fontSize = 11.sp,
            letterSpacing = 0.6.sp,
            color = FormUpColors.TextSecondary
        )
    }
}

// Dropdown picker for selecting the active app language
@Composable
private fun LanguageSelector(
    selected: LanguageOption,
    onSelected: (LanguageOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(size = 38.dp, background = FormUpColors.PrimaryTint) {
                Icon(
                    imageVector = Icons.Filled.Language,
                    contentDescription = null,
                    tint = FormUpColors.Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Language",
                    style = MaterialTheme.typography.titleMedium,
                    color = FormUpColors.TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "App and notification language",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormUpColors.TextSecondary
                )
            }
            Spacer(Modifier.size(8.dp))
            Text(
                text = selected.nativeLabel,
                fontFamily = Mono,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = FormUpColors.PrimaryDeep
            )
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = FormUpColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SupportedLanguages.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.nativeLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (option.code == selected.code) {
                                FormUpColors.PrimaryDeep
                            } else {
                                FormUpColors.TextPrimary
                            }
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Preference toggle row with a title, subtitle, and switch control
@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = FormUpColors.TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = FormUpColors.TextSecondary
            )
        }
        Spacer(Modifier.size(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = FormUpColors.Surface,
                checkedTrackColor = FormUpColors.Primary,
                uncheckedThumbColor = FormUpColors.Surface,
                uncheckedTrackColor = FormUpColors.Hairline,
                uncheckedBorderColor = FormUpColors.Hairline
            )
        )
    }
}

// Summary card displaying team details (home ground, squad, club code)
@Composable
private fun TeamInfoCard(info: TeamInfo, onEdit: () -> Unit) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(size = 38.dp, background = FormUpColors.PrimaryTint) {
                Icon(
                    imageVector = Icons.Filled.Groups,
                    contentDescription = null,
                    tint = FormUpColors.Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = info.teamName,
                    style = MaterialTheme.typography.titleMedium,
                    color = FormUpColors.TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${info.squad} · ${info.season}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FormUpColors.TextSecondary
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Edit team information",
                tint = FormUpColors.TextSecondary,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onEdit)
            )
        }
        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = FormUpColors.Hairline)
        Spacer(Modifier.height(6.dp))
        InfoRow(label = "Home Ground", value = info.homeGround)
        InfoRow(label = "Squad", value = info.squad)
        InfoRow(label = "Season", value = info.season)
        InfoRow(label = "Club Code", value = info.clubCode, mono = true)
    }
}

@Composable
private fun InfoRow(label: String, value: String, mono: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.uppercase(),
            fontFamily = Mono,
            fontSize = 10.sp,
            letterSpacing = 0.6.sp,
            color = FormUpColors.TextSecondary
        )
        Spacer(Modifier.weight(1f))
        if (mono) {
            Text(
                text = value,
                fontFamily = Mono,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FormUpColors.PrimaryDeep
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = FormUpColors.TextPrimary
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1400)
@Composable
private fun ProfileScreenPreview() {
    FormUpTheme {
        ProfileScreen()
    }
}

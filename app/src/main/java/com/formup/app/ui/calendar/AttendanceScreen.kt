package com.formup.app.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.IconBubble
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.home.components.StatusPill
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    state: AttendanceUiState = SampleAttendanceState,
    onBack: () -> Unit = {},
    onEditEvent: () -> Unit = {},
    onNudgeAllNoReplies: () -> Unit = {},
    onRowAction: (RosterEntry) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var nudgedIds by remember { mutableStateOf(setOf<String>()) }
    var markedPresentIds by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FormUpColors.TextPrimary)
                    }
                }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = FormUpColors.TextSecondary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.size(6.dp))
                        Text(state.dateTimeLabel, fontFamily = Mono, fontSize = 11.sp, letterSpacing = 0.4.sp, color = FormUpColors.TextSecondary)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(state.eventTitle, style = MaterialTheme.typography.headlineMedium, color = FormUpColors.TextPrimary)
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = onEditEvent,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FormUpColors.Primary, contentColor = FormUpColors.Surface),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.size(8.dp))
                        Text("Edit Event", fontFamily = Mono, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = FormUpColors.Surface,
                    border = BorderStroke(1.5.dp, FormUpColors.Primary)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("SQUAD STATUS", fontFamily = Mono, fontSize = 11.sp, letterSpacing = 0.5.sp, color = FormUpColors.TextSecondary)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("${state.attending}/${state.squadSize}", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = FormUpColors.TextPrimary)
                            Spacer(Modifier.size(8.dp))
                            Text("Attending", style = MaterialTheme.typography.bodyMedium, color = FormUpColors.TextSecondary)
                        }
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AttendanceStatCard("ATTENDING", state.attending, Icons.Filled.CheckCircle, FormUpColors.Primary, Modifier.weight(1f))
                    AttendanceStatCard("ABSENT", state.absentCount, Icons.Filled.Cancel, FormUpColors.Danger, Modifier.weight(1f))
                    AttendanceStatCard("NO REPLY", state.noReplyCount, Icons.Filled.Help, FormUpColors.AmberIcon, Modifier.weight(1f))
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Roster Details", style = MaterialTheme.typography.headlineMedium, color = FormUpColors.TextPrimary, modifier = Modifier.weight(1f))
                    OutlinedButton(
                        onClick = onNudgeAllNoReplies,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, FormUpColors.Primary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FormUpColors.Primary),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Filled.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Nudge All No-Replies", fontFamily = Mono, fontSize = 11.sp)
                    }
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
                    RosterStatus.entries.forEach { group ->
                        val entries = state.roster.filter { it.status == group }
                        if (entries.isNotEmpty()) {
                            GroupHeader(group, entries.size)
                            entries.forEachIndexed { index, entry ->
                                RosterRow(
                                    entry = entry,
                                    isNudged = entry.id in nudgedIds,
                                    isMarkedPresent = entry.id in markedPresentIds,
                                    onAction = {
                                        when (entry.action) {
                                            RosterRowAction.NUDGE -> nudgedIds = nudgedIds + entry.id
                                            RosterRowAction.MARK_PRESENT -> markedPresentIds = markedPresentIds + entry.id
                                            RosterRowAction.NONE -> Unit
                                        }
                                        onRowAction(entry)
                                    }
                                )
                                if (index != entries.lastIndex) {
                                    HorizontalDivider(color = FormUpColors.Hairline, modifier = Modifier.padding(horizontal = 14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceStatCard(label: String, value: Int, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = FormUpColors.NavIndicator) {
        Column(Modifier.padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(6.dp))
            Text(value.toString(), style = MaterialTheme.typography.headlineMedium, color = FormUpColors.TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(label, fontFamily = Mono, fontSize = 10.sp, letterSpacing = 0.4.sp, color = FormUpColors.TextSecondary)
        }
    }
}

@Composable
private fun GroupHeader(status: RosterStatus, count: Int) {
    val dotColor = when (status) {
        RosterStatus.NO_REPLY -> FormUpColors.AmberIcon
        RosterStatus.ABSENT -> FormUpColors.Danger
        RosterStatus.ATTENDING -> FormUpColors.Primary
    }
    Surface(color = FormUpColors.Background, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(
                Modifier
                    .size(6.dp)
                    .background(dotColor, androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(Modifier.size(6.dp))
            Text(
                "${status.label} ($count)",
                fontFamily = Mono,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
                fontWeight = FontWeight.Medium,
                color = FormUpColors.TextSecondary
            )
        }
    }
}

@Composable
private fun RosterRow(entry: RosterEntry, isNudged: Boolean, isMarkedPresent: Boolean, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBubble(size = 38.dp, background = FormUpColors.PrimaryTint) {
            Text(initialsOf(entry.name), color = FormUpColors.PrimaryDeep, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(entry.name, style = MaterialTheme.typography.titleMedium, color = FormUpColors.TextPrimary)
            Spacer(Modifier.height(2.dp))
            if (entry.statusBadge != null) {
                StatusPill(text = entry.statusBadge, background = FormUpColors.Danger.copy(alpha = 0.12f), contentColor = FormUpColors.Danger)
            } else {
                Text(entry.subtitle, style = MaterialTheme.typography.bodySmall, color = FormUpColors.TextSecondary)
            }
        }

        when (entry.action) {
            RosterRowAction.MARK_PRESENT -> {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = if (isMarkedPresent) "Marked present" else "Mark present",
                    tint = if (isMarkedPresent) FormUpColors.Primary else FormUpColors.TextSecondary,
                    modifier = Modifier.size(22.dp).let {
                        if (!isMarkedPresent) it else it
                    }.then(Modifier.padding(0.dp)).let { base ->
                        androidx.compose.ui.Modifier.size(22.dp)
                    }
                )
            }
            RosterRowAction.NUDGE -> {
                OutlinedButton(
                    onClick = onAction,
                    enabled = !isNudged,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, FormUpColors.Hairline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FormUpColors.TextPrimary),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(if (isNudged) "Nudged" else "Nudge", fontFamily = Mono, fontSize = 11.sp)
                }
            }
            RosterRowAction.NONE -> Unit
        }
    }
}

private fun initialsOf(name: String): String =
    name.trim().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

@Preview(showBackground = true, widthDp = 360, heightDp = 1200)
@Composable
private fun AttendanceScreenPreview() {
    FormUpTheme { AttendanceScreen() }
}
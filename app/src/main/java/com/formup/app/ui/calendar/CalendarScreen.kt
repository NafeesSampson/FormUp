package com.formup.app.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
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
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.home.components.StatusPill
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

@Composable
fun CalendarScreen(
    state: CalendarUiState = SampleCalendarState,
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    onManageTrainingOrLineup: () -> Unit = {},
    onViewMatchDetails: (CalendarEvent.Match) -> Unit = {},
    onViewTeamAttendance: (CalendarEvent.Training) -> Unit = {},
    onNotifications: () -> Unit = {},
    onSelectTab: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(state.selectedDate) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = { FormUpTopBar(hasUnread = true, onNotifications = onNotifications) },
        bottomBar = { FormUpBottomBar(selected = HomeTab.Calendar, onSelect = onSelectTab) }
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.monthLabel,
                        style = MaterialTheme.typography.headlineMedium,
                        color = FormUpColors.Primary,
                        modifier = Modifier.weight(1f)
                    )
                    MonthNavButton(Icons.Filled.ChevronLeft, "Previous month", onPreviousMonth)
                    Spacer(Modifier.size(8.dp))
                    MonthNavButton(Icons.Filled.ChevronRight, "Next month", onNextMonth)
                }
            }

            item {
                MonthGrid(
                    weekdayLetters = state.weekdayLetters,
                    days = state.days,
                    selectedDate = selectedDate,
                    onSelectDate = { selectedDate = it }
                )
            }

            item { ManageEventPill(onClick = onManageTrainingOrLineup) }

            item {
                Text("Upcoming", style = MaterialTheme.typography.headlineMedium, color = FormUpColors.TextPrimary)
            }

            items(state.events, key = { it.id }) { event ->
                when (event) {
                    is CalendarEvent.Match -> MatchEventCard(
                        event = event,
                        onViewDetails = { onViewMatchDetails(event) }
                    )
                    is CalendarEvent.Training -> TrainingEventCard(
                        event = event,
                        onViewTeamAttendance = { onViewTeamAttendance(event) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthNavButton(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = FormUpColors.Surface,
        border = BorderStroke(1.dp, FormUpColors.Hairline),
        modifier = Modifier.size(36.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = description, tint = FormUpColors.TextPrimary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ManageEventPill(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = FormUpColors.AmberTint,
        border = BorderStroke(1.dp, FormUpColors.Amber)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.EditCalendar, contentDescription = null, tint = FormUpColors.AmberIcon, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text("Manage Training or Lineup", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = FormUpColors.AmberIcon)
        }
    }
}

@Composable
private fun MonthGrid(
    weekdayLetters: List<String>,
    days: List<CalendarDay>,
    selectedDate: Int,
    onSelectDate: (Int) -> Unit
) {
    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            weekdayLetters.forEach { letter ->
                Text(
                    text = letter,
                    fontFamily = Mono,
                    fontSize = 11.sp,
                    color = FormUpColors.TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        days.chunked(7).forEach { week ->

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                week.forEach { day ->

                    DayCell(
                        day = day,
                        isSelected =
                            day.isInCurrentMonth &&
                                    day.date == selectedDate,

                        onClick = {
                            if (day.isInCurrentMonth) {
                                onSelectDate(day.date)
                            }
                        },

                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(7 - week.size) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(day: CalendarDay, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val dotColor = when (day.marker) {
        DayMarker.MATCH -> FormUpColors.Primary
        DayMarker.TRAINING -> FormUpColors.AmberIcon
        DayMarker.ACTION -> FormUpColors.Danger
        null -> null
    }
    Column(
        modifier = modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) FormUpColors.Mint else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.date.toString(),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isSelected -> FormUpColors.PrimaryDeep
                !day.isInCurrentMonth -> FormUpColors.TextSecondary.copy(alpha = 0.4f)
                else -> FormUpColors.TextPrimary
            }
        )
        Spacer(Modifier.height(3.dp))
        Box(Modifier.size(5.dp).clip(CircleShape).background(dotColor ?: androidx.compose.ui.graphics.Color.Transparent))
    }
}

@Composable
private fun MatchEventCard(event: CalendarEvent.Match, onViewDetails: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = FormUpColors.Surface,
        border = BorderStroke(1.5.dp, FormUpColors.Primary)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(1f))
                StatusPill(text = event.dateLabel, background = FormUpColors.Mint, contentColor = FormUpColors.PrimaryDeep)
            }
            Spacer(Modifier.height(4.dp))
            Text("vs. ${event.opponent}", style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary)
            Spacer(Modifier.height(10.dp))
            InfoLine(Icons.Filled.AccessTime, event.timeText)
            Spacer(Modifier.height(4.dp))
            InfoLine(Icons.Filled.LocationOn, event.location)
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onViewDetails,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FormUpColors.Primary, contentColor = FormUpColors.Surface),
                modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
                Text("View Details & Lineup", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun TrainingEventCard(event: CalendarEvent.Training, onViewTeamAttendance: () -> Unit) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        Text("TRAINING", fontFamily = Mono, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = FormUpColors.Primary)
        Spacer(Modifier.height(4.dp))
        Text(event.title, style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary)
        Spacer(Modifier.height(10.dp))
        InfoLine(Icons.Filled.AccessTime, event.timeRange)
        Spacer(Modifier.height(4.dp))
        InfoLine(Icons.Filled.LocationOn, event.location)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onViewTeamAttendance,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, FormUpColors.Hairline),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = FormUpColors.Surface, contentColor = FormUpColors.TextPrimary),
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Icon(Icons.Filled.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(8.dp))
            Text("View Team Attendance", fontFamily = Mono, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun InfoLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = FormUpColors.TextSecondary, modifier = Modifier.size(15.dp))
        Spacer(Modifier.size(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = FormUpColors.TextSecondary)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1100)
@Composable
private fun CalendarScreenPreview() {
    FormUpTheme { CalendarScreen() }
}
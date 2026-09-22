package com.formup.app.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.FormUpBottomBar
import com.formup.app.ui.home.components.FormUpTopBar
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.stats.components.DualStatBar
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

// Screen displaying full post-match analysis, team stats, and player ratings
@Composable
fun MatchReportScreen(
    state: MatchReportUiState = SampleMatchReportState,
    onBack: () -> Unit = {},
    onNotifications: () -> Unit = {},
    modifier: Modifier = Modifier,
    onSelectTab: (HomeTab) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            FormUpTopBar(hasUnread = false, onNotifications = onNotifications)
        },
        bottomBar = {
            FormUpBottomBar(
                selected = HomeTab.Stats,
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
                    .padding(start = 4.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Back",
                        tint = FormUpColors.TextPrimary
                    )
                }
                Text(
                    text = "Match Report",
                    style = MaterialTheme.typography.titleMedium,
                    color = FormUpColors.TextSecondary
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { MatchScoreHeader(state.statusLine, state.awayTeam, state.homeTeam) }
                item { OverviewCard(state.overview) }
                item { TacticalNotesCard(state.tacticalNotes) }
                item { TimelineCard(state.timeline) }
                item { TeamStatsCard(state.teamStats) }
                item { MyPerformanceCard(state.myPerformance) }
                item { RatingsCard(state.ratings) }
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FormUpColors.TextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = FormUpColors.TextPrimary
        )
    }
}

// Header banner displaying final match score and team badges
@Composable
private fun MatchScoreHeader(statusLine: String, away: TeamScoreInfo, home: TeamScoreInfo) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = statusLine,
            fontFamily = Mono,
            fontSize = 10.sp,
            letterSpacing = 0.4.sp,
            color = FormUpColors.TextSecondary
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamBadge(team = away, modifier = Modifier.weight(1f))
            Text(
                text = "${away.score} - ${home.score}",
                fontFamily = Mono,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = FormUpColors.TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            TeamBadge(team = home, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TeamBadge(team: TeamScoreInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (team.isFormUp) FormUpColors.Primary else FormUpColors.Surface),
            contentAlignment = Alignment.Center
        ) {
            if (team.isFormUp) {
                Icon(
                    imageVector = Icons.Filled.SportsSoccer,
                    contentDescription = null,
                    tint = FormUpColors.Surface,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text(
                    text = team.abbreviation,
                    fontFamily = Mono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = FormUpColors.TextSecondary
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = team.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (team.isFormUp) FontWeight.Bold else FontWeight.Normal,
            color = FormUpColors.TextPrimary,
            textAlign = TextAlign.Center
        )
    }
}

// Summary paragraph explaining the overall match storyline
@Composable
private fun OverviewCard(overview: String) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(icon = Icons.Filled.Description, title = "Overview")
        Spacer(Modifier.height(10.dp))
        Text(
            text = overview,
            style = MaterialTheme.typography.bodyMedium,
            color = FormUpColors.TextSecondary
        )
    }
}

// Bulleted tactical highlights and observations from coaching staff
@Composable
private fun TacticalNotesCard(notes: List<TacticalNote>) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(icon = Icons.Filled.QueryStats, title = "Tactical Notes")
        Spacer(Modifier.height(10.dp))
        notes.forEachIndexed { index, note ->
            if (index > 0) Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = if (note.positive) Icons.Filled.CheckCircle else Icons.Filled.Circle,
                    contentDescription = null,
                    tint = if (note.positive) FormUpColors.Primary else FormUpColors.Amber,
                    modifier = Modifier
                        .size(15.dp)
                        .padding(top = 2.dp)
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = note.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FormUpColors.TextPrimary
                )
            }
        }
    }
}

// Chronological log of key events (goals, cards, subs)
@Composable
private fun TimelineCard(events: List<TimelineEvent>) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(icon = Icons.Filled.Schedule, title = "Timeline")
        Spacer(Modifier.height(10.dp))
        events.forEachIndexed { index, event ->
            if (index > 0) Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = event.minute,
                    fontFamily = Mono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = FormUpColors.TextPrimary,
                    modifier = Modifier.width(34.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp, end = 10.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(event.type.dotColor)
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = event.headline,
                        style = MaterialTheme.typography.titleMedium,
                        color = FormUpColors.TextPrimary
                    )
                    Text(
                        text = event.detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = FormUpColors.TextSecondary
                    )
                }
            }
        }
    }
}

// Side-by-side comparative team statistics
@Composable
private fun TeamStatsCard(stats: List<DualStat>) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(icon = Icons.Filled.QueryStats, title = "Team Stats")
        Spacer(Modifier.height(14.dp))
        stats.forEachIndexed { index, stat ->
            if (index > 0) Spacer(Modifier.height(14.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stat.leftDisplay,
                        fontFamily = Mono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = FormUpColors.TextSecondary
                    )
                    Text(
                        text = stat.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FormUpColors.TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stat.rightDisplay,
                        fontFamily = Mono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FormUpColors.PrimaryDeep
                    )
                }
                Spacer(Modifier.height(6.dp))
                DualStatBar(leftValue = stat.leftValue, rightValue = stat.rightValue)
            }
        }
    }
}

// Individual performance score badge and match metrics
@Composable
private fun MyPerformanceCard(performance: PerformanceSummary) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(icon = Icons.Filled.Person, title = "My Performance")
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .border(width = 3.dp, color = FormUpColors.Primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = performance.rating,
                        fontFamily = Mono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = FormUpColors.PrimaryDeep
                    )
                    Text(
                        text = performance.ratingLabel,
                        fontFamily = Mono,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                        color = FormUpColors.PrimaryDeep
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PerformanceMetric(label = "MINUTES", value = performance.minutes, modifier = Modifier.weight(1f))
            PerformanceMetric(label = "GOALS", value = performance.goals, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PerformanceMetric(label = "PASS ACC.", value = performance.passAccuracy, modifier = Modifier.weight(1f))
            PerformanceMetric(label = "DISTANCE", value = performance.distance, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = FormUpColors.Hairline)
        Spacer(Modifier.height(14.dp))
        PerformanceMetric(label = "TOP SPEED", value = performance.topSpeed, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun PerformanceMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontFamily = Mono,
            fontSize = 10.sp,
            letterSpacing = 0.4.sp,
            color = FormUpColors.TextSecondary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = FormUpColors.TextPrimary
        )
    }
}

// Squad player ratings and match highlights list
@Composable
private fun RatingsCard(ratings: List<PlayerRating>) {
    SectionCard(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(icon = Icons.Filled.Groups, title = "FormUp FC Ratings")
        Spacer(Modifier.height(6.dp))
        ratings.forEachIndexed { index, rating ->
            if (index > 0) HorizontalDivider(color = FormUpColors.Hairline)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${rating.rank}",
                    fontFamily = Mono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = FormUpColors.TextSecondary,
                    modifier = Modifier.width(30.dp)
                )
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = rating.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = FormUpColors.TextPrimary
                        )
                        if (rating.isStandout) {
                            Spacer(Modifier.size(4.dp))
                            Icon(
                                imageVector = Icons.Filled.SportsSoccer,
                                contentDescription = "Man of the match",
                                tint = FormUpColors.Amber,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    Text(
                        text = rating.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = FormUpColors.TextSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (rating.isStandout) FormUpColors.AmberTint else FormUpColors.PrimaryTint
                ) {
                    Text(
                        text = rating.rating,
                        fontFamily = Mono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (rating.isStandout) FormUpColors.AmberIcon else FormUpColors.PrimaryDeep,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1600)
@Composable
private fun MatchReportScreenPreview() {
    FormUpTheme {
        MatchReportScreen()
    }
}

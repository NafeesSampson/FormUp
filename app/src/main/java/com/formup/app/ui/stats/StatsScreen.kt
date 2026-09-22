package com.formup.app.ui.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.FormUpBottomBar
import com.formup.app.ui.home.components.FormUpTopBar
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.stats.components.StatProgressBar
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

private enum class PerformerTab { Goals, Assists }

// Team analytics screen showing performance summaries, leaderboards, and recent match stats
@Composable
fun StatsScreen(
    state: StatsUiState = SampleStatsState,
    onFilter: () -> Unit = {},
    onInputStats: () -> Unit = {},
    onExportPdf: () -> Unit = {},
    onViewFullMatchReport: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onSelectTab: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var performerTab by remember { mutableStateOf(PerformerTab.Goals) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            FormUpTopBar(hasUnread = false, onNotifications = onNotifications)
        },
        bottomBar = {
            FormUpBottomBar(selected = HomeTab.Stats, onSelect = onSelectTab)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Team Analytics",
                        style = MaterialTheme.typography.headlineMedium,
                        color = FormUpColors.TextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${state.squadName} · ${state.season}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FormUpColors.TextSecondary
                    )
                }
            }

            // Quick action toolbar (Filter, Input Stats, Export PDF)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onFilter,
                        modifier = Modifier.height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, FormUpColors.Hairline),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FormUpColors.TextPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(text = "Filter", fontFamily = Mono, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onInputStats,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FormUpColors.Primary,
                            contentColor = FormUpColors.Surface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PlaylistAddCheck,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(text = "Input Stats", fontFamily = Mono, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = onExportPdf,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FormUpColors.PrimaryDeep,
                            contentColor = FormUpColors.Surface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(text = "Export PDF", fontFamily = Mono, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // High-level season metrics grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.summary.chunked(2).forEach { rowStats ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowStats.forEach { stat ->
                                SummaryStatCard(stat = stat, modifier = Modifier.weight(1f))
                            }
                            if (rowStats.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Top performers leaderboard (Goals / Assists)
            item {
                SectionCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Top Performers",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        PerformerTabPill(
                            label = "Goals",
                            selected = performerTab == PerformerTab.Goals,
                            onClick = { performerTab = PerformerTab.Goals }
                        )
                        Spacer(Modifier.size(6.dp))
                        PerformerTabPill(
                            label = "Assists",
                            selected = performerTab == PerformerTab.Assists,
                            onClick = { performerTab = PerformerTab.Assists }
                        )
                    }
                    Spacer(Modifier.height(6.dp))

                    val leaders = if (performerTab == PerformerTab.Goals) state.goalsLeaders else state.assistsLeaders
                    leaders.forEachIndexed { index, performer ->
                        if (index > 0) {
                            HorizontalDivider(color = FormUpColors.Hairline)
                        }
                        TopPerformerRow(performer)
                    }
                }
            }

            // Quick summary of the last completed match
            item {
                SectionCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Last Match Analysis vs. ${state.lastMatch.opponent} (${state.lastMatch.resultText})",
                        style = MaterialTheme.typography.titleMedium,
                        color = FormUpColors.TextPrimary
                    )
                    Spacer(Modifier.height(14.dp))
                    state.lastMatch.rows.forEachIndexed { index, row ->
                        if (index > 0) Spacer(Modifier.height(14.dp))
                        MatchStatRowView(row)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "View Full Match Report",
                        fontFamily = Mono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = FormUpColors.Primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onViewFullMatchReport)
                    )
                }
            }
        }
    }
}

// Single metric card with a title, current value, and progress indicator
@Composable
private fun SummaryStatCard(stat: SummaryStat, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = FormUpColors.Surface,
        border = BorderStroke(if (stat.highlighted) 1.5.dp else 1.dp, if (stat.highlighted) FormUpColors.Primary else FormUpColors.Hairline)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = stat.label,
                fontFamily = Mono,
                fontSize = 11.sp,
                color = FormUpColors.TextSecondary
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.titleLarge,
                    color = FormUpColors.TextPrimary
                )
                if (stat.delta != null) {
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = stat.delta,
                        fontFamily = Mono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = FormUpColors.PrimaryDeep
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            StatProgressBar(progress = stat.progress, fillColor = stat.barColor)
        }
    }
}

// Filter button pill for toggling between top performers categories
@Composable
private fun PerformerTabPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) FormUpColors.Mint else Color.Transparent,
        border = if (!selected) BorderStroke(1.dp, FormUpColors.Hairline) else null,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontFamily = Mono,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) FormUpColors.PrimaryDeep else FormUpColors.TextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// Player entry row on the leaderboards table
@Composable
private fun TopPerformerRow(performer: TopPerformer) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color = performer.badgeColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = performer.number.toString(),
                fontFamily = Mono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = performer.badgeTextColor
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = performer.name,
                style = MaterialTheme.typography.titleMedium,
                color = FormUpColors.TextPrimary
            )
            Text(
                text = "${performer.position} · ${performer.matches} Matches",
                style = MaterialTheme.typography.bodySmall,
                color = FormUpColors.TextSecondary
            )
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = FormUpColors.NavIndicator
        ) {
            Text(
                text = performer.statValue,
                fontFamily = Mono,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = FormUpColors.TextPrimary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

// Single progress bar row in the last match analysis card
@Composable
private fun MatchStatRowView(row: MatchStatRow) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = row.label,
                style = MaterialTheme.typography.bodyMedium,
                color = FormUpColors.TextPrimary
            )
            Text(
                text = row.valueText,
                fontFamily = Mono,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = FormUpColors.TextSecondary
            )
        }
        Spacer(Modifier.height(8.dp))
        StatProgressBar(progress = row.progress, fillColor = row.barColor)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1400)
@Composable
private fun StatsScreenPreview() {
    FormUpTheme {
        StatsScreen()
    }
}

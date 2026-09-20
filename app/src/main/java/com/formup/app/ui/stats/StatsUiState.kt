package com.formup.app.ui.stats

import com.formup.app.ui.theme.FormUpColors

data class SummaryStat(
    val label: String,
    val value: String,
    val delta: String? = null,
    val progress: Float,
    val barColor: androidx.compose.ui.graphics.Color = FormUpColors.Primary,
    val highlighted: Boolean = false
)

data class TopPerformer(
    val number: Int,
    val name: String,
    val position: String,
    val matches: Int,
    val statValue: String,
    val badgeColor: androidx.compose.ui.graphics.Color = FormUpColors.NavIndicator,
    val badgeTextColor: androidx.compose.ui.graphics.Color = FormUpColors.TextPrimary
)

data class MatchStatRow(
    val label: String,
    val valueText: String,
    val progress: Float,
    val barColor: androidx.compose.ui.graphics.Color = FormUpColors.Primary
)

data class LastMatchAnalysis(
    val opponent: String,
    val resultText: String,
    val rows: List<MatchStatRow>
)

data class StatsUiState(
    val squadName: String,
    val season: String,
    val summary: List<SummaryStat>,
    val goalsLeaders: List<TopPerformer>,
    val assistsLeaders: List<TopPerformer>,
    val lastMatch: LastMatchAnalysis
)

val SampleStatsState = StatsUiState(
    squadName = "U18 Varsity Squad",
    season = "2026 Season",
    summary = listOf(
        SummaryStat(
            label = "Win Rate",
            value = "78%",
            delta = "↑5%",
            progress = 0.78f,
            barColor = FormUpColors.Primary,
            highlighted = true
        ),
        SummaryStat(
            label = "Goals / Match",
            value = "2.4",
            progress = 0.48f,
            barColor = FormUpColors.Amber
        ),
        SummaryStat(
            label = "Clean Sheets",
            value = "8 / 12 Games",
            progress = 0.67f,
            barColor = FormUpColors.Primary
        ),
        SummaryStat(
            label = "Possession Avg",
            value = "58%",
            progress = 0.58f,
            barColor = FormUpColors.Primary
        )
    ),
    goalsLeaders = listOf(
        TopPerformer(
            number = 10,
            name = "Marcus Rashford",
            position = "Forward",
            matches = 12,
            statValue = "14 Goals",
            badgeColor = FormUpColors.Amber,
            badgeTextColor = FormUpColors.Surface
        ),
        TopPerformer(
            number = 9,
            name = "Erling Haaland",
            position = "Striker",
            matches = 11,
            statValue = "11 Goals"
        ),
        TopPerformer(
            number = 7,
            name = "Bukayo Saka",
            position = "Winger",
            matches = 12,
            statValue = "8 Goals"
        )
    ),
    assistsLeaders = listOf(
        TopPerformer(
            number = 8,
            name = "Kevin De Bruyne",
            position = "Midfielder",
            matches = 12,
            statValue = "10 Assists",
            badgeColor = FormUpColors.Amber,
            badgeTextColor = FormUpColors.Surface
        ),
        TopPerformer(
            number = 11,
            name = "Jack Grealish",
            position = "Winger",
            matches = 10,
            statValue = "7 Assists"
        ),
        TopPerformer(
            number = 4,
            name = "Declan Rice",
            position = "Midfielder",
            matches = 12,
            statValue = "5 Assists"
        )
    ),
    lastMatch = LastMatchAnalysis(
        opponent = "Rovers FC",
        resultText = "W 2-0",
        rows = listOf(
            MatchStatRow("Possession", "62% - 38%", progress = 0.62f, barColor = FormUpColors.Primary),
            MatchStatRow("Pass Accuracy", "85% (412 passes)", progress = 0.85f, barColor = FormUpColors.Amber),
            MatchStatRow("Shots on Target", "8 / 14", progress = 0.57f, barColor = FormUpColors.Primary)
        )
    )
)
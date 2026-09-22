package com.formup.app.ui.stats

import androidx.compose.ui.graphics.Color
import com.formup.app.ui.theme.FormUpColors

data class PlayerStatEntry(
    val id: String,
    val number: Int,
    val name: String,
    val position: String,
    val goals: Int,
    val assists: Int,
    val minutesPlayed: Int = 0,
    val rating: Double = 0.0,
    val badgeColor: Color = FormUpColors.Primary,
    val badgeTextColor: Color = FormUpColors.Surface
)

data class FixtureOption(val id: String, val label: String)

data class StatsInputUiState(
    val fixtureId: String,
    val resultBadge: String,
    val matchDate: String,
    val opponent: String,
    val competition: String,
    val homeLabel: String,
    val awayLabel: String,
    val homeScore: Int,
    val awayScore: Int,
    val availableFixtures: List<FixtureOption> = emptyList(),
    val players: List<PlayerStatEntry>
)

val SampleStatsInputState = StatsInputUiState(
    fixtureId = "sample",
    resultBadge = "FT 3-1 (W)",
    matchDate = "Oct 14, 2023",
    opponent = "vs. Metro United",
    competition = "Varsity Boys Soccer · League Match",
    homeLabel = "FORMUP FC",
    awayLabel = "OPPONENT",
    homeScore = 3,
    awayScore = 1,
    players = listOf(
        PlayerStatEntry(
            id = "j-smith",
            number = 10,
            name = "J. Smith",
            position = "Forward",
            goals = 2,
            assists = 1,
            badgeColor = FormUpColors.Primary,
            badgeTextColor = FormUpColors.Surface
        ),
        PlayerStatEntry(
            id = "m-johnson",
            number = 4,
            name = "M. Johnson",
            position = "Defender",
            goals = 0,
            assists = 0,
            badgeColor = FormUpColors.NavIndicator,
            badgeTextColor = FormUpColors.TextPrimary
        )
    )
)
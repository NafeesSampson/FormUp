package com.formup.app.ui.stats

import androidx.compose.ui.graphics.Color
import com.formup.app.ui.theme.FormUpColors

data class TeamScoreInfo(
    val abbreviation: String,
    val name: String,
    val score: Int,
    val isFormUp: Boolean = false
)

data class TacticalNote(
    val text: String,
    val positive: Boolean = true
)

enum class TimelineEventType(val dotColor: Color) {
    Goal(FormUpColors.Primary),
    Card(FormUpColors.Amber),
    Substitution(FormUpColors.TextSecondary)
}

data class TimelineEvent(
    val minute: String,
    val type: TimelineEventType,
    val headline: String,
    val detail: String
)

data class DualStat(
    val label: String,
    val leftValue: Float,
    val rightValue: Float,
    val leftDisplay: String,
    val rightDisplay: String
)

data class PerformanceSummary(
    val rating: String,
    val ratingLabel: String,
    val minutes: String,
    val goals: String,
    val passAccuracy: String,
    val distance: String,
    val topSpeed: String
)

data class PlayerRating(
    val rank: Int,
    val name: String,
    val note: String,
    val rating: String,
    val isStandout: Boolean = false
)

data class MatchReportUiState(
    val statusLine: String,
    val awayTeam: TeamScoreInfo,
    val homeTeam: TeamScoreInfo,
    val overview: String,
    val tacticalNotes: List<TacticalNote>,
    val timeline: List<TimelineEvent>,
    val teamStats: List<DualStat>,
    val myPerformance: PerformanceSummary,
    val ratings: List<PlayerRating>
)

val SampleMatchReportState = MatchReportUiState(
    statusLine = "FULL TIME · OCT 14, 2023 · CITY STADIUM",
    awayTeam = TeamScoreInfo(abbreviation = "RFC", name = "Rovers FC", score = 0),
    homeTeam = TeamScoreInfo(abbreviation = "FUP", name = "FormUp FC", score = 2, isFormUp = true),
    overview = "Clinical finishing and a solid defensive line secured the win away from home. Midfield control in the second half was decisive.",
    tacticalNotes = listOf(
        TacticalNote("Shifted to 4-2-3-1 after 60 mins neutralized their flank attacks.", positive = true),
        TacticalNote("High press in the opening 20 mins created early chances.", positive = false)
    ),
    timeline = listOf(
        TimelineEvent("24'", TimelineEventType.Goal, "Goal (FormUp FC)", "J. Smith (Assist: M. Doe)"),
        TimelineEvent("42'", TimelineEventType.Card, "Yellow Card (Rovers)", "T. Johnson"),
        TimelineEvent("65'", TimelineEventType.Substitution, "Substitution (FormUp)", "In: K. Lee, Out: R. Davis"),
        TimelineEvent("78'", TimelineEventType.Goal, "Goal (FormUp FC)", "A. Chen")
    ),
    teamStats = listOf(
        DualStat("Possession", 38f, 62f, "38%", "62%"),
        DualStat("Total Shots", 6f, 14f, "6", "14"),
        DualStat("Shots on Target", 2f, 8f, "2", "8"),
        DualStat("Pass Accuracy", 74f, 88f, "74%", "88%")
    ),
    myPerformance = PerformanceSummary(
        rating = "8.5",
        ratingLabel = "EXCELLENT",
        minutes = "85'",
        goals = "1",
        passAccuracy = "92%",
        distance = "9.2km",
        topSpeed = "28.4 km/h"
    ),
    ratings = listOf(
        PlayerRating(1, "David De Gea", "GK · 3 Saves", "7.5"),
        PlayerRating(4, "Virgil van Dijk", "CB · 5 Clearances", "8.2"),
        PlayerRating(8, "M. Doe", "CM · 1 Assist, 92% Pass Acc", "8.5"),
        PlayerRating(9, "J. Smith", "ST · 1 Goal, 4 Shots", "9.1", isStandout = true)
    )
)
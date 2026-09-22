package com.formup.app.ui.home

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.HealthAndSafety


//data classes that are passed to the api
data class NextMatch(
    val opponent: String,
    val date: String,
    val kickoff: String,
    val venue: String
)

data class Availability(
    val fit: Int,
    val doubtful: Int,
    val out: Int
) {
    val total: Int get() = fit + doubtful + out
}

data class LastMatch(
    val homeTeam: String,
    val homeScore: Int,
    val awayTeam: String,
    val awayScore: Int,
    val status: String,
    val stats: List<Pair<String, String>>
)

enum class UpdateTone { Notice, Medical }

data class TeamUpdate(
    val title: String,
    val timestamp: String,
    val body: String,
    val tone: UpdateTone
) {
    val icon: ImageVector
        get() = when (tone) {
            UpdateTone.Notice -> Icons.Filled.Campaign
            UpdateTone.Medical -> Icons.Filled.HealthAndSafety
        }
}

data class HomeUiState(
    val coachName: String,
    val prompt: String,
    val nextMatch: NextMatch,
    val availability: Availability,
    val lastMatch: LastMatch,
    val updates: List<TeamUpdate>,
    val unreadNotifications: Boolean
)

//dummy info for testing
val SampleHomeState = HomeUiState(
    coachName = "Coach Thomas",
    prompt = "Ready for the big match this weekend?",
    nextMatch = NextMatch(
        opponent = "vs. Metro City FC",
        date = "Oct 28",
        kickoff = "14:00 Kickoff",
        venue = "Riverside Stadium, Field A"
    ),
    availability = Availability(fit = 18, doubtful = 2, out = 2),
    lastMatch = LastMatch(
        homeTeam = "FormUp FC",
        homeScore = 3,
        awayTeam = "Eastside United",
        awayScore = 1,
        status = "FT",
        stats = listOf(
            "Possession" to "62%",
            "Shots on Target" to "8"
        )
    ),
    updates = listOf(
        TeamUpdate(
            title = "Field Change Notice",
            timestamp = "2h ago",
            body = "Thursday practice moved to Field B due to maintenance on the main pitch.",
            tone = UpdateTone.Notice
        ),
        TeamUpdate(
            title = "Medical Clearance",
            timestamp = "Yesterday",
            body = "Alex has been cleared by physio to return to light training this week.",
            tone = UpdateTone.Medical
        )
    ),
    unreadNotifications = true
)

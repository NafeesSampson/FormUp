package com.formup.app.data

//enum to track whether a player is healthy and ready to play
enum class AvailabilityStatus(val label: String) {
    Fit("Fit"),
    Doubtful("Doubtful"),
    Out("Out")
}

//stores a players individual match statistics
data class MatchLine(
    val goals: Int = 0,
    val assists: Int = 0,
    val minutes: Int = 0,
    val rating: Double = 0.0
)

//player data
data class Player(
    val id: String,
    val number: Int,
    val name: String,
    val position: String,
    val status: AvailabilityStatus = AvailabilityStatus.Fit,
    val inLineup: Boolean = false
)

//things that can happen durng a match
enum class MatchEventType {
    Goal,
    Card,
    Substitution
}

//this is a single event during a match
data class MatchEvent(
    val minute: Int,
    val type: MatchEventType,
    val headline: String,
    val detail: String
)

//represents a scheduled or completed match fixture
data class Fixture(
    val id: String,
    val opponent: String,
    val competition: String,
    val dateLabel: String,
    val kickoff: String,
    val venue: String,
    val isHome: Boolean = true,
    val played: Boolean = false,
    val teamScore: Int = 0,
    val opponentScore: Int = 0,

    // These values are zero when the API does not supply them. Might implement in POE
    val possession: Int = 0,
    val passAccuracy: Int = 0,
    val opponentPassAccuracy: Int = 0,
    val shots: Int = 0,
    val shotsOnTarget: Int = 0,
    val opponentShots: Int = 0,
    val opponentShotsOnTarget: Int = 0,

    val playerStats: Map<String, MatchLine> = emptyMap(),
    val events: List<MatchEvent> = emptyList(),
    val tacticalNotes: List<String> = emptyList()
) {

    val resultLetter: String
        get() = when {
            !played -> "-"
            teamScore > opponentScore -> "W"
            teamScore < opponentScore -> "L"
            else -> "D"
        }

    val scoreLine: String
        get() = "$teamScore-$opponentScore"

    val resultText: String
        get() =
            if (played) {
                "$resultLetter $scoreLine"
            } else {
                kickoff
            }
}

enum class UpdateKind {
    Notice,
    Medical
}

data class TeamUpdateItem(
    val id: String,
    val title: String,
    val timestamp: String,
    val body: String,
    val kind: UpdateKind
)

data class ActivityEntry(
    val id: String,
    val title: String,
    val subtitle: String,
    val date: String,
    val isTraining: Boolean
)

enum class NotificationKind {
    Attendance,
    Schedule,
    Fitness,
    Stats
}

data class AppNotification(
    val id: String,
    val kind: NotificationKind,
    val title: String,
    val timestamp: String,
    val body: String,
    val isRead: Boolean = false,
    val actionLabel: String? = null,
    val fixtureId: String? = null
)

//holds profile information and settings for the coach
data class CoachProfile(
    val name: String,
    val role: String,
    val badges: List<String>,
    val languageCode: String,
    val matchReminders: Boolean,
    val weeklySummary: Boolean
)

//holds general profile details for the team
data class TeamProfile(
    val name: String,
    val squad: String,
    val season: String,
    val homeGround: String,
    val clubCode: String,
    val inviteLink: String
)
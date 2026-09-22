package com.formup.app.ui.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

// Single key performance indicator shown on the profile grid
data class SeasonStat(
    val label: String,
    val value: String,
    val icon: ImageVector
)

enum class ActivityTone { Match, Training }

// Event log item for recent matches or training sessions
data class ActivityItem(
    val title: String,
    val subtitle: String,
    val date: String,
    val tone: ActivityTone
) {
    val icon: ImageVector
        get() = when (tone) {
            ActivityTone.Match -> Icons.Filled.SportsSoccer
            ActivityTone.Training -> Icons.Filled.FitnessCenter
        }
}

// Summary details of the coach's assigned club and squad
data class TeamInfo(
    val teamName: String,
    val squad: String,
    val season: String,
    val homeGround: String,
    val clubCode: String
)

data class LanguageOption(
    val code: String,
    val label: String,
    val nativeLabel: String
)

// List of supported app localization languages
val SupportedLanguages = listOf(
    LanguageOption("en", "English", "English"),
    LanguageOption("af", "Afrikaans", "Afrikaans"),
    LanguageOption("xh", "isiXhosa", "isiXhosa"),
    LanguageOption("zu", "isiZulu", "isiZulu"),
    LanguageOption("pt", "Portuguese", "Português"),
    LanguageOption("es", "Spanish", "Español")
)

// UI state for the profile and settings screen
data class ProfileUiState(
    val name: String,
    val role: String,
    val badges: List<String>,
    val seasonStats: List<SeasonStat>,
    val recentActivity: List<ActivityItem>,
    val teamInfo: TeamInfo,
    val language: LanguageOption,
    val matchReminders: Boolean,
    val weeklySummary: Boolean,
    val unreadNotifications: Boolean
)

// Mock profile state for UI previews
val SampleProfileState = ProfileUiState(
    name = "Thomas Newman",
    role = "Coach",
    badges = listOf("Elite", "Born: 2005"),
    seasonStats = listOf(
        SeasonStat("Goals", "12", Icons.Filled.SportsScore),
        SeasonStat("Assists", "8", Icons.Filled.Handshake),
        SeasonStat("Matches", "18", Icons.Filled.EventAvailable),
        SeasonStat("Minutes", "1,420", Icons.Filled.Timer)
    ),
    recentActivity = listOf(
        ActivityItem(
            title = "Match vs. Metro United",
            subtitle = "Played 85 mins · 1 Goal",
            date = "OCT 14",
            tone = ActivityTone.Match
        ),
        ActivityItem(
            title = "Tactical Training",
            subtitle = "Full session completed",
            date = "OCT 12",
            tone = ActivityTone.Training
        ),
        ActivityItem(
            title = "Match vs. Valley FC",
            subtitle = "Played 90 mins",
            date = "OCT 07",
            tone = ActivityTone.Match
        )
    ),
    teamInfo = TeamInfo(
        teamName = "FormUp FC",
        squad = "First Team",
        season = "2025/26",
        homeGround = "Riverside Stadium, Field A",
        clubCode = "FORMUP-9X2J"
    ),
    language = SupportedLanguages.first(),
    matchReminders = true,
    weeklySummary = false,
    unreadNotifications = true
)

package com.formup.app.ui.navigation

import com.formup.app.ui.home.components.HomeTab

/**
 * Represents all top-level and detail navigation destinations within the FormUp app.
 */
sealed interface Destination {
    /** Main dashboard screen showing upcoming fixtures and quick actions. */
    object Home : Destination

    /** Calendar screen displaying scheduled matches and training events. */
    object Calendar : Destination

    /** Roster and squad management screen. */
    object Team : Destination

    /** Overall team and player performance analytics screen. */
    object Stats : Destination

    /** User account settings, coach info, and app configuration screen. */
    object Profile : Destination

    /** Screen for creating or editing scheduled calendar events. */
    object ManageEvent : Destination

    /** Notification center displaying unread alerts and updates. */
    object Notifications : Destination

    /** Screen for generating and sharing invite links/codes to recruit players. */
    object InvitePlayer : Destination

    /** Screen for editing coach and team profile details. */
    object EditProfile : Destination

    /** Form for manually adding a new player to the squad. */
    object AddPlayer : Destination

    /**
     * Detailed information for a specific match fixture.
     */
    data class MatchDetails(val fixtureId: String) : Destination

    /**
     * Player attendance tracking and RSVP list for a specific event.
     */
    data class Attendance(val fixtureId: String) : Destination

    /**
     * Starting XI and bench lineup selector for a specific match.
     */
    data class Lineup(val fixtureId: String) : Destination

    /**
     * Form for logging player match statistics (goals, assists, cards, etc.).
     */
    data class StatsInput(val fixtureId: String) : Destination

    /**
     * Post-match summary report and statistics view.
     */
    data class MatchReport(val fixtureId: String) : Destination
}

/**
 * Maps a [Destination] to its parent [HomeTab] in the bottom navigation bar.
 * This determines which tab is active when navigating to sub-screens or deep links.
 */
val Destination.tab: HomeTab
    get() = when (this) {
        Destination.Calendar -> HomeTab.Calendar

        Destination.Team,
        Destination.InvitePlayer,
        Destination.AddPlayer -> HomeTab.Team

        Destination.Stats,
        is Destination.StatsInput,
        is Destination.MatchReport -> HomeTab.Stats

        Destination.Profile,
        Destination.EditProfile -> HomeTab.Profile

        else -> HomeTab.Home
    }

/**
 * Maps a bottom navigation [HomeTab] to its root [Destination].
 */
val HomeTab.destination: Destination
    get() = when (this) {
        HomeTab.Home -> Destination.Home
        HomeTab.Calendar -> Destination.Calendar
        HomeTab.Team -> Destination.Team
        HomeTab.Stats -> Destination.Stats
        HomeTab.Profile -> Destination.Profile
    }

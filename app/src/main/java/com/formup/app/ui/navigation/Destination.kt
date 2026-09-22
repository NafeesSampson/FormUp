package com.formup.app.ui.navigation

import com.formup.app.ui.home.components.HomeTab

sealed interface Destination {
    object Home : Destination
    object Calendar : Destination
    object Team : Destination
    object Stats : Destination
    object Profile : Destination

    object Notifications : Destination
    object InvitePlayer : Destination
    object EditProfile : Destination
    object AddPlayer : Destination

    data class MatchDetails(val fixtureId: String) : Destination
    data class Attendance(val fixtureId: String) : Destination
    data class Lineup(val fixtureId: String) : Destination
    data class StatsInput(val fixtureId: String) : Destination
    data class MatchReport(val fixtureId: String) : Destination
}


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

val HomeTab.destination: Destination
    get() = when (this) {
        HomeTab.Home -> Destination.Home
        HomeTab.Calendar -> Destination.Calendar
        HomeTab.Team -> Destination.Team
        HomeTab.Stats -> Destination.Stats
        HomeTab.Profile -> Destination.Profile
    }
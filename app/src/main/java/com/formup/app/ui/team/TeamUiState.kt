package com.formup.app.ui.team

data class SquadPlayer(
    val id: String,
    val fullName: String,
    val position: String,
    val status: PlayerStatus
)

enum class PlayerStatus { FIT, DOUBT, OUT }

data class TeamUiState(
    val players: List<SquadPlayer> = emptyList()
) {
    val total: Int get() = players.size
    val fit: Int get() = players.count { it.status == PlayerStatus.FIT }
    val doubt: Int get() = players.count { it.status == PlayerStatus.DOUBT }
    val out: Int get() = players.count { it.status == PlayerStatus.OUT }
}

val SampleTeamState = TeamUiState()
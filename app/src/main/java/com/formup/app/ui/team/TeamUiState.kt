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

/** Static placeholder content, same convention as SampleNotificationsState. */
val SampleTeamState = TeamUiState(
    players = listOf(
        SquadPlayer("1", "Alex Rivera", "Midfielder", PlayerStatus.FIT),
        SquadPlayer("2", "Sarah Jenkins", "Defender", PlayerStatus.FIT),
        SquadPlayer("3", "Marcus Thorne", "Forward", PlayerStatus.DOUBT)
    )
)
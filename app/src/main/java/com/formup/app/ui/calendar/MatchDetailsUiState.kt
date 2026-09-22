package com.formup.app.ui.calendar

data class LineupPlayer(
    val number: Int,
    val name: String,
    val xPercent: Float,
    val yPercent: Float,
    val isGoalkeeper: Boolean = false
)

data class SubPlayer(val number: Int, val name: String, val position: String)

data class MatchDetailsUiState(
    val competitionTag: String,
    val title: String,
    val dateLabel: String,
    val timeLabel: String,
    val venueName: String,
    val venueDetail: String,
    val inCount: Int,
    val outCount: Int,
    val tbdCount: Int,
    val squadSize: Int,
    val formation: String,
    val startingXi: List<LineupPlayer>,
    val substitutes: List<SubPlayer>
) {
    val attending: Int get() = inCount
}

//dummy information used for testing
val SampleMatchDetailsState = MatchDetailsUiState(
    competitionTag = "League Match",
    title = "Match vs. Metro United",
    dateLabel = "Saturday, Oct 28",
    timeLabel = "10:00 AM Kickoff",
    venueName = "Metro Sports Complex",
    venueDetail = "Field 4",
    inCount = 18,
    outCount = 2,
    tbdCount = 4,
    squadSize = 24,
    formation = "4-3-3",
    startingXi = listOf(
        LineupPlayer(11, "Davis", 0.18f, 0.14f),
        LineupPlayer(9, "Smith", 0.50f, 0.12f),
        LineupPlayer(7, "Chen", 0.82f, 0.14f),
        LineupPlayer(8, "Johnson", 0.24f, 0.42f),
        LineupPlayer(10, "Silva", 0.50f, 0.40f),
        LineupPlayer(6, "Patel", 0.76f, 0.42f),
        LineupPlayer(3, "O'Brien", 0.16f, 0.68f),
        LineupPlayer(5, "Kim", 0.40f, 0.70f),
        LineupPlayer(4, "Garcia", 0.62f, 0.70f),
        LineupPlayer(2, "Lee", 0.86f, 0.68f),
        LineupPlayer(1, "Martinez", 0.50f, 0.90f, isGoalkeeper = true)
    ),
    substitutes = listOf(
        SubPlayer(12, "Williams", "GK"),
        SubPlayer(14, "Nguyen", "DEF"),
        SubPlayer(17, "Toure", "MID"),
        SubPlayer(21, "Rossi", "MID")
    )
)
package com.formup.app.ui.calendar

enum class RosterStatus(val label: String) {
    NO_REPLY("NO REPLY"),
    ABSENT("ABSENT"),
    ATTENDING("ATTENDING")
}

enum class RosterRowAction { NUDGE, MARK_PRESENT, NONE }

data class RosterEntry(
    val id: String,
    val name: String,
    val subtitle: String,          // position, e.g. "Forward"
    val statusBadge: String? = null, // e.g. "INJURED" — shown instead of subtitle when present
    val status: RosterStatus,
    val action: RosterRowAction = RosterRowAction.NONE
)

data class AttendanceUiState(
    val dateTimeLabel: String,
    val eventTitle: String,
    val squadSize: Int,
    val attending: Int,
    val absentCount: Int,
    val noReplyCount: Int,
    val roster: List<RosterEntry>
)

val SampleAttendanceState = AttendanceUiState(
    dateTimeLabel = "OCT 26, 6:30 PM · MAIN PITCH",
    eventTitle = "Tactical Training",
    squadSize = 24,
    attending = 18,
    absentCount = 2,
    noReplyCount = 4,
    roster = listOf(
        RosterEntry("1", "David Kim", "Forward", status = RosterStatus.NO_REPLY, action = RosterRowAction.MARK_PRESENT),
        RosterEntry("2", "Elias Lund", "Defender", status = RosterStatus.NO_REPLY, action = RosterRowAction.NUDGE),
        RosterEntry("3", "Sarah Jenkins", "Defender", statusBadge = "INJURED", status = RosterStatus.ABSENT),
        RosterEntry("4", "Marcus Johnson", "Midfielder", status = RosterStatus.ATTENDING, action = RosterRowAction.NUDGE)
    )
)
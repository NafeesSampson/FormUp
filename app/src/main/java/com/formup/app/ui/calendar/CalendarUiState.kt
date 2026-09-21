package com.formup.app.ui.calendar

data class CalendarDay(
    val date: Int,
    val dayLetter: String,
    val isInCurrentMonth: Boolean = true,
    val marker: DayMarker? = null
)

enum class DayMarker { MATCH, TRAINING, ACTION }

data class ActionBanner(
    val title: String,
    val message: String
)

sealed class CalendarEvent {
    abstract val id: String

    data class Match(
        override val id: String,
        val opponent: String,
        val timeText: String,
        val location: String
    ) : CalendarEvent()

    data class Training(
        override val id: String,
        val title: String,
        val timeRange: String,
        val location: String,
        val myAttendance: AttendanceChoice?
    ) : CalendarEvent()
}

enum class AttendanceChoice { COMING, MAYBE, OUT }

data class CalendarUiState(
    val monthLabel: String,
    val actionBanner: ActionBanner? = null,
    val weekdayLetters: List<String> = listOf("S", "M", "T", "W", "T", "F", "S"),
    val days: List<CalendarDay>,
    val selectedDate: Int,
    val selectedDateLabel: String,
    val events: List<CalendarEvent>
)

/**
 * Static placeholder content. Wiring a real day -> events map is for whoever
 * connects this to the shared calendar / training-session data.
 */
val SampleCalendarState = CalendarUiState(
    monthLabel = "October 2026",
    actionBanner = ActionBanner(
        title = "Action Required",
        message = "Please confirm your attendance for the upcoming tournament by Friday."
    ),
    days = listOf(
        CalendarDay(22, "S", isInCurrentMonth = false),
        CalendarDay(23, "M", marker = DayMarker.MATCH),
        CalendarDay(24, "T"),
        CalendarDay(25, "W", marker = DayMarker.TRAINING),
        CalendarDay(26, "T"),
        CalendarDay(27, "F", marker = DayMarker.MATCH),
        CalendarDay(28, "S", marker = DayMarker.ACTION)
    ),
    selectedDate = 28,
    selectedDateLabel = "Saturday, October 28",
    events = listOf(
        CalendarEvent.Match(
            id = "match-metro-city",
            opponent = "Metro City FC",
            timeText = "14:00 Kickoff (Arrive 13:00)",
            location = "Riverside Stadium"
        ),
        CalendarEvent.Training(
            id = "training-first-team",
            title = "First Team Training",
            timeRange = "18:00 - 19:30",
            location = "Training Pitch 2",
            myAttendance = AttendanceChoice.COMING
        )
    )
)
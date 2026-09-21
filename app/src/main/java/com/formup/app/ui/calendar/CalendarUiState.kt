package com.formup.app.ui.calendar

data class CalendarDay(
    val date: Int,
    val dayLetter: String,
    val isInCurrentMonth: Boolean = true,
    val marker: DayMarker? = null
)

enum class DayMarker { MATCH, TRAINING, ACTION }

sealed class CalendarEvent {
    abstract val id: String

    data class Match(
        override val id: String,
        val opponent: String,
        val dateLabel: String,
        val timeText: String,
        val location: String,
        val formation: String? = null
    ) : CalendarEvent()

    data class Training(
        override val id: String,
        val title: String,
        val timeRange: String,
        val location: String
    ) : CalendarEvent()
}

data class CalendarUiState(
    val monthLabel: String,
    val weekdayLetters: List<String> = listOf("S", "M", "T", "W", "T", "F", "S"),
    val days: List<CalendarDay>,
    val selectedDate: Int,
    val events: List<CalendarEvent>
)

/**
 * Static placeholder content. The day grid below is not generated from
 * [events] — its marker dots stay fixed until whoever wires real month/date
 * logic connects the two.
 */
val SampleCalendarState = CalendarUiState(
    monthLabel = "October 2026",
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
    events = listOf(
        CalendarEvent.Match(
            id = "match-metro-city",
            opponent = "Metro City FC",
            dateLabel = "Saturday, Oct 28",
            timeText = "14:00 Kickoff (Arrive 13:00)",
            location = "Riverside Stadium"
        ),
        CalendarEvent.Training(
            id = "training-first-team",
            title = "First Team Training",
            timeRange = "18:00 - 19:30",
            location = "Training Pitch 2"
        )
    )
)
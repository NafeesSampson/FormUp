package com.formup.app.ui.calendar

//data classes for events such as matches and trainings. these data classes is what is passed to api
enum class EventKind {
    TRAINING,
    MATCH
}

data class TrainingFormState(
    val date: String = "",
    val time: String = "",
    val notes: String = "",
    val dateError: String? = null
)

data class MatchFormState(
    val opponent: String = "",
    val venue: String = "Home",
    val date: String = "",
    val time: String = "",
    val opponentError: String? = null,
    val dateError: String? = null
)
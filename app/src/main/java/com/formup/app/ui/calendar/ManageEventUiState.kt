package com.formup.app.ui.calendar

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
enum class EventKind { TRAINING, LINEUP }

data class FormationSpot(val xPercent: Float, val yPercent: Float, val isGoalkeeper: Boolean = false)

data class Formation(val label: String, val spots: List<FormationSpot>)

/**
 * Formation *templates* only — a shape the coach picks, tagged onto the
 * created match. Assigning actual players to the 11 spots is a separate,
 * bigger feature (needs the real roster wired in) that this doesn't attempt.
 */
val Formation433 = Formation(
    label = "4-3-3",
    spots = listOf(
        FormationSpot(0.50f, 0.90f, isGoalkeeper = true),
        FormationSpot(0.16f, 0.68f), FormationSpot(0.40f, 0.70f), FormationSpot(0.62f, 0.70f), FormationSpot(0.86f, 0.68f),
        FormationSpot(0.24f, 0.42f), FormationSpot(0.50f, 0.40f), FormationSpot(0.76f, 0.42f),
        FormationSpot(0.18f, 0.14f), FormationSpot(0.50f, 0.12f), FormationSpot(0.82f, 0.14f)
    )
)

val Formation442 = Formation(
    label = "4-4-2",
    spots = listOf(
        FormationSpot(0.50f, 0.90f, isGoalkeeper = true),
        FormationSpot(0.16f, 0.68f), FormationSpot(0.40f, 0.70f), FormationSpot(0.62f, 0.70f), FormationSpot(0.86f, 0.68f),
        FormationSpot(0.16f, 0.42f), FormationSpot(0.40f, 0.44f), FormationSpot(0.62f, 0.44f), FormationSpot(0.86f, 0.42f),
        FormationSpot(0.35f, 0.14f), FormationSpot(0.65f, 0.14f)
    )
)

fun toIsoUtc(dateText: String, timeText: String): String? {
    val combined = if (timeText.isBlank()) "$dateText 00:00" else "$dateText $timeText"
    val parsed = runCatching {
        SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).apply { isLenient = false }.parse(combined)
    }.getOrNull() ?: return null
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(parsed)
}

val AvailableFormations = listOf(Formation433, Formation442)

data class TrainingFormState(
    val title: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val titleError: String? = null,
    val dateError: String? = null,
    val locationError: String? = null
)

data class LineupFormState(
    val opponent: String = "",
    val date: String = "",
    val kickoffTime: String = "",
    val arrivalTime: String = "",
    val location: String = "",
    val isHome: Boolean = true,
    val formation: Formation = AvailableFormations.first(),
    val opponentError: String? = null,
    val dateError: String? = null,
    val locationError: String? = null
)

private val inputDateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US).apply { isLenient = false }
private val displayDateFormat = SimpleDateFormat("EEEE, MMM d", Locale.US)

/** Best-effort "Saturday, Oct 28" formatting; falls back to the raw text if it doesn't parse. */
fun formatDateLabel(raw: String): String =
    runCatching { inputDateFormat.parse(raw)?.let { displayDateFormat.format(it) } }.getOrNull() ?: raw
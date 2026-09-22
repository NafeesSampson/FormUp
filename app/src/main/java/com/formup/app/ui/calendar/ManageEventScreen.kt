package com.formup.app.ui.calendar

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.data.FormUpApi
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventScreen(
    onBack: () -> Unit = {},
    onEventCreated: (CalendarEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var kind by remember { mutableStateOf(EventKind.TRAINING) }
    var trainingForm by remember { mutableStateOf(TrainingFormState()) }
    var lineupForm by remember { mutableStateOf(LineupFormState()) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun createTraining() {
        val titleError = if (trainingForm.title.isBlank()) "Give the session a name" else null
        val dateError = if (trainingForm.date.isBlank()) "Enter a date" else null
        if (titleError != null || dateError != null) {
            trainingForm = trainingForm.copy(titleError = titleError, dateError = dateError)
            return
        }
        val isoDate = toIsoUtc(trainingForm.date, trainingForm.startTime)
        if (isoDate == null) {
            trainingForm = trainingForm.copy(dateError = "Enter a valid date (MM/DD/YYYY)")
            return
        }
        if (saving) return
        saving = true
        scope.launch {
            try {
                val notes = "${trainingForm.title.trim()} — ${trainingForm.location.trim()}".trimEnd(' ', '—')
                val createdId = FormUpApi().createSession(isoDate, notes)

                val timeRange = when {
                    trainingForm.startTime.isNotBlank() && trainingForm.endTime.isNotBlank() -> "${trainingForm.startTime} - ${trainingForm.endTime}"
                    trainingForm.startTime.isNotBlank() -> trainingForm.startTime
                    else -> "Time TBC"
                }

                onEventCreated(
                    CalendarEvent.Training(
                        id = createdId,
                        title = trainingForm.title.trim(),
                        timeRange = timeRange,
                        location = trainingForm.location.trim()
                    )
                )
            } catch (e: Exception) {
                Toast.makeText(context, e.message ?: "Could not create training session", Toast.LENGTH_LONG).show()
            } finally {
                saving = false
            }
        }
    }

    fun createLineup() {
        val opponentError = if (lineupForm.opponent.isBlank()) "Enter the opponent" else null
        val dateError = if (lineupForm.date.isBlank()) "Enter a date" else null
        if (opponentError != null || dateError != null) {
            lineupForm = lineupForm.copy(opponentError = opponentError, dateError = dateError)
            return
        }
        val isoDate = toIsoUtc(lineupForm.date, lineupForm.kickoffTime)
        if (isoDate == null) {
            lineupForm = lineupForm.copy(dateError = "Enter a valid date (MM/DD/YYYY)")
            return
        }
        if (saving) return
        saving = true
        scope.launch {
            try {
                // Must pass exactly "Home" or "Away" for the API venue parameter
                val venueParam = if (lineupForm.isHome) "Home" else "Away"
                val createdId = FormUpApi().createMatch(
                    opponent = lineupForm.opponent.trim(),
                    matchDateIso = isoDate,
                    venue = venueParam
                )

                val timeText = buildString {
                    append(lineupForm.kickoffTime.ifBlank { "Time TBC" })
                    append(" Kickoff")
                    if (lineupForm.arrivalTime.isNotBlank()) append(" (Arrive ${lineupForm.arrivalTime})")
                }

                onEventCreated(
                    CalendarEvent.Match(
                        id = createdId,
                        opponent = lineupForm.opponent.trim(),
                        dateLabel = formatDateLabel(lineupForm.date),
                        timeText = timeText,
                        location = lineupForm.location.trim(),
                        formation = lineupForm.formation.label
                    )
                )
            } catch (e: Exception) {
                Toast.makeText(context, e.message ?: "Could not create match", Toast.LENGTH_LONG).show()
            } finally {
                saving = false
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Manage Training or Match", style = MaterialTheme.typography.titleMedium, color = FormUpColors.TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FormUpColors.TextPrimary)
                    }
                }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { EventTypeToggle(selected = kind, onSelect = { kind = it }) }

            when (kind) {
                EventKind.TRAINING -> item {
                    SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
                        FieldLabel("Session Name")
                        FormField(trainingForm.title, { trainingForm = trainingForm.copy(title = it, titleError = null) }, "e.g., Tactical Training", trainingForm.titleError)

                        Spacer(Modifier.height(14.dp))
                        FieldLabel("Date")
                        FormField(trainingForm.date, { trainingForm = trainingForm.copy(date = it, dateError = null) }, "MM/DD/YYYY", trainingForm.dateError)

                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) {
                                FieldLabel("Start Time")
                                FormField(trainingForm.startTime, { trainingForm = trainingForm.copy(startTime = it) }, "18:00")
                            }
                            Column(Modifier.weight(1f)) {
                                FieldLabel("End Time")
                                FormField(trainingForm.endTime, { trainingForm = trainingForm.copy(endTime = it) }, "19:30")
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        FieldLabel("Location")
                        FormField(trainingForm.location, { trainingForm = trainingForm.copy(location = it) }, "e.g., Training Pitch 2")
                    }
                }

                EventKind.LINEUP -> item {
                    SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
                        FieldLabel("Opponent")
                        FormField(lineupForm.opponent, { lineupForm = lineupForm.copy(opponent = it, opponentError = null) }, "e.g., Camps Bay FC", lineupForm.opponentError)

                        Spacer(Modifier.height(14.dp))
                        FieldLabel("Venue Type")
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilterChip(
                                selected = lineupForm.isHome,
                                onClick = { lineupForm = lineupForm.copy(isHome = true) },
                                label = { Text("Home Match") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = !lineupForm.isHome,
                                onClick = { lineupForm = lineupForm.copy(isHome = false) },
                                label = { Text("Away Match") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(14.dp))
                        FieldLabel("Date")
                        FormField(lineupForm.date, { lineupForm = lineupForm.copy(date = it, dateError = null) }, "MM/DD/YYYY", lineupForm.dateError)

                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) {
                                FieldLabel("Kickoff Time")
                                FormField(lineupForm.kickoffTime, { lineupForm = lineupForm.copy(kickoffTime = it) }, "14:00")
                            }
                            Column(Modifier.weight(1f)) {
                                FieldLabel("Arrival Time")
                                FormField(lineupForm.arrivalTime, { lineupForm = lineupForm.copy(arrivalTime = it) }, "13:00")
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        FieldLabel("Location / Pitch")
                        FormField(lineupForm.location, { lineupForm = lineupForm.copy(location = it) }, "e.g., Main Stadium Pitch")
                    }
                }
            }

            item {
                Button(
                    onClick = { if (kind == EventKind.TRAINING) createTraining() else createLineup() },
                    enabled = !saving,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FormUpColors.Primary, contentColor = FormUpColors.Surface),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(
                        text = if (saving) "Saving..." else if (kind == EventKind.TRAINING) "Create Training" else "Create Match",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun EventTypeToggle(selected: EventKind, onSelect: (EventKind) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(FormUpColors.NavIndicator, RoundedCornerShape(10.dp))
            .padding(3.dp)
    ) {
        listOf(EventKind.TRAINING to "Training", EventKind.LINEUP to "Match").forEach { (value, label) ->
            val isSelected = value == selected
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onSelect(value) },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) FormUpColors.Surface else androidx.compose.ui.graphics.Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 9.dp)) {
                    Text(
                        label,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (isSelected) FormUpColors.Primary else FormUpColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontSize = 11.sp, color = FormUpColors.TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
}

@Composable
private fun FormField(value: String, onValueChange: (String) -> Unit, placeholder: String, error: String? = null) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = FormUpColors.TextSecondary) },
            singleLine = true,
            isError = error != null,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FormUpColors.Surface,
                unfocusedContainerColor = FormUpColors.Surface,
                focusedBorderColor = FormUpColors.Primary,
                unfocusedBorderColor = FormUpColors.Hairline,
                cursorColor = FormUpColors.Primary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        error?.let {
            Text(it, fontSize = 10.sp, color = FormUpColors.Danger, modifier = Modifier.padding(top = 4.dp, start = 2.dp))
        }
    }
}
@Preview(showBackground = true, widthDp = 360, heightDp = 1100)
@Composable
private fun ManageEventScreenPreview() {
    FormUpTheme { ManageEventScreen() }
}
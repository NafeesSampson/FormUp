package com.formup.app.ui.calendar

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono
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

    fun createTraining() {
        val titleError = if (trainingForm.title.isBlank()) "Give the session a name" else null
        val dateError = if (trainingForm.date.isBlank()) "Enter a date" else null
        val locationError = if (trainingForm.location.isBlank()) "Enter a location" else null
        if (titleError != null || dateError != null || locationError != null) {
            trainingForm = trainingForm.copy(titleError = titleError, dateError = dateError, locationError = locationError)
            return
        }
        val timeRange = when {
            trainingForm.startTime.isNotBlank() && trainingForm.endTime.isNotBlank() -> "${trainingForm.startTime} - ${trainingForm.endTime}"
            trainingForm.startTime.isNotBlank() -> trainingForm.startTime
            else -> "Time TBC"
        }
        onEventCreated(
            CalendarEvent.Training(
                id = UUID.randomUUID().toString(),
                title = trainingForm.title.trim(),
                timeRange = timeRange,
                location = trainingForm.location.trim()
            )
        )
    }

    fun createLineup() {
        val opponentError = if (lineupForm.opponent.isBlank()) "Enter the opponent" else null
        val dateError = if (lineupForm.date.isBlank()) "Enter a date" else null
        val locationError = if (lineupForm.location.isBlank()) "Enter a location" else null
        if (opponentError != null || dateError != null || locationError != null) {
            lineupForm = lineupForm.copy(opponentError = opponentError, dateError = dateError, locationError = locationError)
            return
        }
        val timeText = buildString {
            append(lineupForm.kickoffTime.ifBlank { "Time TBC" })
            append(" Kickoff")
            if (lineupForm.arrivalTime.isNotBlank()) append(" (Arrive ${lineupForm.arrivalTime})")
        }
        onEventCreated(
            CalendarEvent.Match(
                id = UUID.randomUUID().toString(),
                opponent = lineupForm.opponent.trim(),
                dateLabel = formatDateLabel(lineupForm.date),
                timeText = timeText,
                location = lineupForm.location.trim(),
                formation = lineupForm.formation.label
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Manage Training or Lineup", style = MaterialTheme.typography.titleMedium, color = FormUpColors.TextPrimary) },
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
                        FormField(trainingForm.date, { trainingForm = trainingForm.copy(date = it, dateError = null) }, "mm/dd/yyyy", trainingForm.dateError)

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
                        FormField(trainingForm.location, { trainingForm = trainingForm.copy(location = it, locationError = null) }, "e.g., Training Pitch 2", trainingForm.locationError)
                    }
                }

                EventKind.LINEUP -> item {
                    SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
                        FieldLabel("Opponent")
                        FormField(lineupForm.opponent, { lineupForm = lineupForm.copy(opponent = it, opponentError = null) }, "e.g., Metro City FC", lineupForm.opponentError)

                        Spacer(Modifier.height(14.dp))
                        FieldLabel("Date")
                        FormField(lineupForm.date, { lineupForm = lineupForm.copy(date = it, dateError = null) }, "mm/dd/yyyy", lineupForm.dateError)

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
                        FieldLabel("Location")
                        FormField(lineupForm.location, { lineupForm = lineupForm.copy(location = it, locationError = null) }, "e.g., Riverside Stadium", lineupForm.locationError)
                    }
                }
            }

            if (kind == EventKind.LINEUP) {
                item {
                    SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
                        Text("Formation", style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Pick a shape for the starting XI. Assigning players comes later.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FormUpColors.TextSecondary
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AvailableFormations.forEach { formation ->
                                FormationOption(
                                    formation = formation,
                                    selected = lineupForm.formation == formation,
                                    onClick = { lineupForm = lineupForm.copy(formation = formation) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { if (kind == EventKind.TRAINING) createTraining() else createLineup() },
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FormUpColors.Primary, contentColor = FormUpColors.Surface),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(
                        if (kind == EventKind.TRAINING) "Create Training" else "Create Lineup",
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
        listOf(EventKind.TRAINING to "Training", EventKind.LINEUP to "Lineup").forEach { (value, label) ->
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
private fun FormationOption(formation: Formation, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = FormUpColors.Surface,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) FormUpColors.Primary else FormUpColors.Hairline)
    ) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(FormUpColors.Primary)
            ) {
                formation.spots.forEach { spot ->
                    Box(
                        modifier = Modifier
                            .align(BiasAlignment(spot.xPercent * 2f - 1f, spot.yPercent * 2f - 1f))
                            .size(if (spot.isGoalkeeper) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (spot.isGoalkeeper) FormUpColors.Amber else FormUpColors.Surface)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                formation.label,
                fontFamily = Mono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (selected) FormUpColors.Primary else FormUpColors.TextPrimary
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontFamily = Mono, fontSize = 11.sp, color = FormUpColors.TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
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
            Text(it, fontFamily = Mono, fontSize = 10.sp, color = FormUpColors.Danger, modifier = Modifier.padding(top = 4.dp, start = 2.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1100)
@Composable
private fun ManageEventScreenPreview() {
    FormUpTheme { ManageEventScreen() }
}
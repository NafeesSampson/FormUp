package com.formup.app.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.data.FormUpApi
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventScreen(
    onBack: () -> Unit = {},
    onEventCreated: (CalendarEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var kind by remember { mutableStateOf(EventKind.TRAINING) }

    var trainingForm by remember {
        mutableStateOf(TrainingFormState())
    }

    var matchForm by remember {
        mutableStateOf(MatchFormState())
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var saving by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun createTraining() {
        val dateError =
            if (trainingForm.date.isBlank()) {
                "Select a session date"
            } else {
                null
            }

        if (dateError != null) {
            trainingForm = trainingForm.copy(dateError = dateError)
            return
        }

        if (trainingForm.time.isBlank()) {
            return
        }

        val isoDate = createIsoDateTime(
            trainingForm.date,
            trainingForm.time
        )

        if (isoDate == null) {
            trainingForm = trainingForm.copy(
                dateError = "Invalid date or time"
            )
            return
        }

        if (saving) return

        saving = true

        scope.launch {
            try {
                val createdId = FormUpApi().createSession(
                    sessionDateIso = isoDate,
                    notes = trainingForm.notes.trim().ifBlank { null }
                )

                onEventCreated(
                    CalendarEvent.Training(
                        id = createdId,
                        title = "Training Session",
                        timeRange = trainingForm.time,
                        location = ""
                    )
                )
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    context,
                    e.message ?: "Could not create training session",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            } finally {
                saving = false
            }
        }
    }

    fun createMatch() {
        val opponentError =
            if (matchForm.opponent.isBlank()) {
                "Enter the opponent"
            } else {
                null
            }

        val dateError =
            if (matchForm.date.isBlank()) {
                "Select a match date"
            } else {
                null
            }

        if (opponentError != null || dateError != null) {
            matchForm = matchForm.copy(
                opponentError = opponentError,
                dateError = dateError
            )
            return
        }

        if (matchForm.time.isBlank()) {
            return
        }

        val isoDate = createIsoDateTime(
            matchForm.date,
            matchForm.time
        )

        if (isoDate == null) {
            matchForm = matchForm.copy(
                dateError = "Invalid date or time"
            )
            return
        }

        if (saving) return

        saving = true

        scope.launch {
            try {
                val createdId = FormUpApi().createMatch(
                    opponent = matchForm.opponent.trim(),
                    matchDateIso = isoDate,
                    venue = matchForm.venue
                )

                onEventCreated(
                    CalendarEvent.Match(
                        id = createdId,
                        opponent = matchForm.opponent.trim(),
                        dateLabel = matchForm.date,
                        timeText = matchForm.time,
                        location = matchForm.venue,
                        formation = null
                    )
                )
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    context,
                    e.message ?: "Could not create match",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            } finally {
                saving = false
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Text(
                        "Create Event",
                        style = MaterialTheme.typography.titleMedium,
                        color = FormUpColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FormUpColors.TextPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            EventTypeToggle(
                selected = kind,
                onSelect = {
                    kind = it
                    showDatePicker = false
                    showTimePicker = false
                }
            )

            when (kind) {
                EventKind.MATCH -> {
                    SectionCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(18.dp)
                    ) {
                        FieldLabel("Opponent")

                        FormField(
                            value = matchForm.opponent,
                            onValueChange = {
                                matchForm = matchForm.copy(
                                    opponent = it,
                                    opponentError = null
                                )
                            },
                            placeholder = "e.g. Camps Bay FC",
                            error = matchForm.opponentError
                        )

                        Spacer(Modifier.height(16.dp))

                        FieldLabel("Venue")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilterChip(
                                selected = matchForm.venue == "Home",
                                onClick = {
                                    matchForm = matchForm.copy(venue = "Home")
                                },
                                label = { Text("Home") },
                                modifier = Modifier.weight(1f)
                            )

                            FilterChip(
                                selected = matchForm.venue == "Away",
                                onClick = {
                                    matchForm = matchForm.copy(venue = "Away")
                                },
                                label = { Text("Away") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        FieldLabel("Match Date")

                        DateTimeField(
                            value = matchForm.date.ifBlank { "Select date" },
                            onClick = { showDatePicker = true }
                        )

                        matchForm.dateError?.let {
                            Text(
                                text = it,
                                color = FormUpColors.Danger,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 4.dp, start = 2.dp)
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        FieldLabel("Kickoff Time")

                        DateTimeField(
                            value = matchForm.time.ifBlank { "Select time" },
                            onClick = { showTimePicker = true }
                        )
                    }
                }

                EventKind.TRAINING -> {
                    SectionCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(18.dp)
                    ) {
                        FieldLabel("Session Date")

                        DateTimeField(
                            value = trainingForm.date.ifBlank { "Select date" },
                            onClick = { showDatePicker = true }
                        )

                        trainingForm.dateError?.let {
                            Text(
                                text = it,
                                color = FormUpColors.Danger,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 4.dp, start = 2.dp)
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        FieldLabel("Session Time")

                        DateTimeField(
                            value = trainingForm.time.ifBlank { "Select time" },
                            onClick = { showTimePicker = true }
                        )

                        Spacer(Modifier.height(14.dp))

                        FieldLabel("Notes")

                        OutlinedTextField(
                            value = trainingForm.notes,
                            onValueChange = {
                                trainingForm = trainingForm.copy(notes = it)
                            },
                            placeholder = {
                                Text("Optional notes", color = FormUpColors.TextSecondary)
                            },
                            minLines = 3,
                            maxLines = 5,
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
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (kind == EventKind.MATCH) {
                        createMatch()
                    } else {
                        createTraining()
                    }
                },
                enabled = !saving,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FormUpColors.Primary,
                    contentColor = FormUpColors.Surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = when {
                        saving -> "Saving..."
                        kind == EventKind.MATCH -> "Create Match"
                        else -> "Create Training"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val date = Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneOffset.UTC)
                                .toLocalDate()

                            val formatted = date.format(
                                DateTimeFormatter.ofPattern("dd MMM yyyy")
                            )

                            if (kind == EventKind.MATCH) {
                                matchForm = matchForm.copy(date = formatted, dateError = null)
                            } else {
                                trainingForm = trainingForm.copy(date = formatted, dateError = null)
                            }
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = 18,
            initialMinute = 0,
            is24Hour = false
        )

        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTime = String.format(
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )

                        if (kind == EventKind.MATCH) {
                            matchForm = matchForm.copy(time = selectedTime)
                        } else {
                            trainingForm = trainingForm.copy(time = selectedTime)
                        }

                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    TimeInput(state = timePickerState)
                }
            }
        )
    }
}

@Composable
private fun EventTypeToggle(
    selected: EventKind,
    onSelect: (EventKind) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(FormUpColors.NavIndicator, RoundedCornerShape(10.dp))
            .padding(3.dp)
    ) {
        listOf(
            EventKind.TRAINING to "Training",
            EventKind.MATCH to "Match"
        ).forEach { (value, label) ->
            val isSelected = value == selected

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onSelect(value) },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) FormUpColors.Surface else Color.Transparent
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 9.dp)
                ) {
                    Text(
                        text = label,
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
private fun DateTimeField(
    value: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = FormUpColors.Surface,
                disabledBorderColor = FormUpColors.Hairline,
                disabledTextColor = FormUpColors.TextPrimary,
                disabledPlaceholderColor = FormUpColors.TextSecondary
            )
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = FormUpColors.TextSecondary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null
) {
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
            Text(
                text = it,
                fontSize = 10.sp,
                color = FormUpColors.Danger,
                modifier = Modifier.padding(top = 4.dp, start = 2.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun createIsoDateTime(
    dateText: String,
    timeText: String
): String? {
    return try {
        val date = LocalDate.parse(
            dateText,
            DateTimeFormatter.ofPattern("dd MMM yyyy")
        )

        val time = LocalTime.parse(
            timeText,
            DateTimeFormatter.ofPattern("HH:mm")
        )

        val dateTime = date.atTime(time)

        dateTime
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    } catch (_: DateTimeParseException) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun ManageEventScreenPreview() {
    FormUpTheme {
        ManageEventScreen()
    }
}
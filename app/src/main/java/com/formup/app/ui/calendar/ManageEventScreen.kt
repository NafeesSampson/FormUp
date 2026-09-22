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

// Main screen for creating new calendar events (Training Sessions or Matches)
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventScreen(
    onBack: () -> Unit = {},                            // Callback when user taps top back button
    onEventCreated: (CalendarEvent) -> Unit = {},       // Callback fired after successful event creation
    modifier: Modifier = Modifier
) {
    // Tracks selected event type tab (defaults to Training)
    var kind by remember { mutableStateOf(EventKind.TRAINING) }

    // Form input states for Training and Match sessions
    var trainingForm by remember {
        mutableStateOf(TrainingFormState())
    }

    var matchForm by remember {
        mutableStateOf(MatchFormState())
    }

    // Visibility flags for Date and Time picker popups
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Loading flag to prevent multiple API submit requests at once
    var saving by remember { mutableStateOf(false) }

    // Scope for running async network requests safely within Compose lifecycle
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Validates inputs and submits a new Training session to the backend server
    fun createTraining() {
        // Validate date field selection
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

        // Convert user-selected date and time strings into an ISO date time string
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

        // Async API network call that is executed inside the coroutine scope
        scope.launch {
            try {
                val createdId = FormUpApi().createSession(
                    sessionDateIso = isoDate,
                    notes = trainingForm.notes.trim().ifBlank { null }
                )

                // Return newly created object back to caller screen
                onEventCreated(
                    CalendarEvent.Training(
                        id = createdId,
                        title = "Training Session",
                        timeRange = trainingForm.time,
                        location = ""
                    )
                )
            } catch (e: Exception) {
                // Show error message toast on network or server error
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

    // Validates inputs and submits a new Match event to the backend server
    fun createMatch() {
        // Validate required fields (Opponent name and Date)
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

        // Convert user-selected date and time strings into ISO format
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

        // Async API request sending match data
        scope.launch {
            try {
                val createdId = FormUpApi().createMatch(
                    opponent = matchForm.opponent.trim(),
                    matchDateIso = isoDate,
                    venue = matchForm.venue
                )

                // Return newly created Match event
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
                // Show error feedback toast on network failure
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

    // Top-level screen scaffold with Top Bar header
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

            // Tab bar toggle to switch between Training and Match form inputs
            EventTypeToggle(
                selected = kind,
                onSelect = {
                    kind = it
                    showDatePicker = false
                    showTimePicker = false
                }
            )

            // Dynamically renders Match inputs or Training inputs based on active tab
            when (kind) {
                EventKind.MATCH -> {
                    SectionCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(18.dp)
                    ) {
                        FieldLabel("Opponent")

                        // Opponent name input field
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

                        // Home/Away toggle chip filter options
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

                        // Read-only field opening date picker dialog on tap
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

                        // Read-only field opening time picker dialog on tap
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

                        // Date picker input field trigger
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

                        // Time picker input field trigger
                        DateTimeField(
                            value = trainingForm.time.ifBlank { "Select time" },
                            onClick = { showTimePicker = true }
                        )

                        Spacer(Modifier.height(14.dp))

                        FieldLabel("Notes")

                        // Optional multiline notes text box
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

            // Main event creation submit button (Disabled while waiting for API response) to make sure multiple requests arent sent
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

    // Material 3 Date Picker Dialog logic
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            // Format selected epoch milliseconds into readable date string ("dd MMM yyyy")
                            val date = Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneOffset.UTC)
                                .toLocalDate()

                            val formatted = date.format(
                                DateTimeFormatter.ofPattern("dd MMM yyyy")
                            )

                            // Update corresponding form state
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

    // Material 3 Time Picker Dialog logic
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
                        // Format selected hour and minute into "HH:mm" time string
                        val selectedTime = String.format(
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )

                        // Update corresponding form state
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

// Segmented tab control component to select event category (Training vs Match)
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

// Clickable read-only text field used as a button to launch Date or Time pickers
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
        // Click overlay box to ensure clicks still register over disabled text fields
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}

// Small section title header text above form inputs
@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = FormUpColors.TextSecondary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

// Reusable text input field with optional validation error message text below
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

// Helper function converting formatted date and time strings to required format for api
@RequiresApi(Build.VERSION_CODES.O)
private fun createIsoDateTime(
    dateText: String,
    timeText: String
): String? {
    return try {
        // Parsing "dd MMM yyyy" format
        val date = LocalDate.parse(
            dateText,
            DateTimeFormatter.ofPattern("dd MMM yyyy")
        )

        // Parsing "HH:mm" 24-hour time format
        val time = LocalTime.parse(
            timeText,
            DateTimeFormatter.ofPattern("HH:mm")
        )

        val dateTime = date.atTime(time)

        // Converting to correct format for date that api needs
        dateTime
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    } catch (_: DateTimeParseException) {
        null
    }
}

// Preview composable to render layout inside Android Studio design editor
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun ManageEventScreenPreview() {
    FormUpTheme {
        ManageEventScreen()
    }
}
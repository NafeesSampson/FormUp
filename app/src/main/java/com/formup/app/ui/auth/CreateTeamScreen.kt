package com.formup.app.ui.auth

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.auth.components.AuthFieldLabel
import com.formup.app.ui.auth.components.AuthTextField
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTeamScreen(
    onBack: () -> Unit = {},
    onCancel: () -> Unit = {},
    onTeamCreated: (CreatedTeam) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var form by remember { mutableStateOf(TeamFormState()) }

    fun update(block: (TeamFormState) -> TeamFormState) {
        form = block(form)
    }

    fun create() {
        val nameError = if (form.teamName.isBlank()) "Enter a team name" else null
        val sportError = if (form.sport.isBlank()) "Select a sport" else null
        val levelError = if (form.ageGroupLevel.isBlank()) "Select an age group or level" else null
        if (nameError != null || sportError != null || levelError != null) {
            form = form.copy(nameError = nameError, sportError = sportError, levelError = levelError)
            return
        }
        onTeamCreated(
            CreatedTeam(
                teamName = form.teamName.trim(),
                sport = form.sport,
                ageGroupLevel = form.ageGroupLevel,
                seasonStartDate = form.seasonStartDate,
                logoUri = form.logoUri,
                joinCode = generateTeamCode()
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Create Team", style = MaterialTheme.typography.titleLarge, color = FormUpColors.Primary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FormUpColors.TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = ::create) {
                        Icon(Icons.Filled.Check, contentDescription = "Create team", tint = FormUpColors.Primary)
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
            item {
                SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(20.dp)) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(FormUpColors.NavIndicator)
                                    .border(1.dp, FormUpColors.Hairline, CircleShape)
                                    .clickable { /* photo picker wiring goes here */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.AddPhotoAlternate,
                                    contentDescription = "Upload team logo",
                                    tint = FormUpColors.TextSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(FormUpColors.Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.FileUpload,
                                    contentDescription = null,
                                    tint = FormUpColors.Surface,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Team Logo", style = MaterialTheme.typography.titleMedium, color = FormUpColors.TextPrimary)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Upload a recognizable badge for your squad.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FormUpColors.TextSecondary
                        )
                    }
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = FormUpColors.Primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(8.dp))
                        Text("Core Details", style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary)
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(Modifier.width(48.dp).height(3.dp).background(FormUpColors.Primary, RoundedCornerShape(2.dp)))
                    Spacer(Modifier.height(16.dp))

                    AuthFieldLabel("Team Name")
                    AuthTextField(
                        value = form.teamName,
                        onValueChange = { update { s -> s.copy(teamName = it, nameError = null) } },
                        placeholder = "e.g., Westside Warriors",
                        error = form.nameError
                    )

                    Spacer(Modifier.height(14.dp))
                    AuthFieldLabel("Sport")
                    OptionDropdown(
                        selected = form.sport,
                        placeholder = "Select Sport...",
                        options = SportOptions,
                        onSelect = { update { s -> s.copy(sport = it, sportError = null) } },
                        error = form.sportError
                    )

                    Spacer(Modifier.height(14.dp))
                    AuthFieldLabel("Age Group / Level")
                    OptionDropdown(
                        selected = form.ageGroupLevel,
                        placeholder = "Select Level...",
                        options = AgeGroupLevelOptions,
                        onSelect = { update { s -> s.copy(ageGroupLevel = it, levelError = null) } },
                        error = form.levelError
                    )
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
                    AuthFieldLabel("Season Start Date")
                    AuthTextField(
                        value = form.seasonStartDate,
                        onValueChange = { update { s -> s.copy(seasonStartDate = it) } },
                        placeholder = "mm/dd/yyyy"
                    )
                }
            }

            item { Spacer(Modifier.height(2.dp)) }

            item {
                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, FormUpColors.Primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FormUpColors.Primary),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Cancel", style = MaterialTheme.typography.titleMedium)
                }
            }

            item {
                Button(
                    onClick = ::create,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FormUpColors.Primary, contentColor = FormUpColors.Surface),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Filled.GroupAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Create Team", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun OptionDropdown(
    selected: String,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(FormUpColors.Surface, RoundedCornerShape(10.dp))
                .border(1.dp, if (error != null) FormUpColors.Danger else FormUpColors.Hairline, RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                selected.ifBlank { placeholder },
                color = if (selected.isBlank()) FormUpColors.TextSecondary else FormUpColors.TextPrimary
            )
            Spacer(Modifier.weight(1f))
            Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = FormUpColors.TextSecondary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option); expanded = false })
            }
        }
        if (error != null) {
            Text(error, fontFamily = Mono, fontSize = 11.sp, color = FormUpColors.Danger, modifier = Modifier.padding(top = 4.dp, start = 2.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1300)
@Composable
private fun CreateTeamScreenPreview() {
    FormUpTheme { CreateTeamScreen() }
}
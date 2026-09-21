package com.formup.app.ui.team

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.IconBubble
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerScreen(
    onBack: () -> Unit = {},
    onSave: (SquadPlayer) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var form by remember { mutableStateOf(AddPlayerFormState()) }

    fun update(block: (AddPlayerFormState) -> AddPlayerFormState) {
        form = block(form)
    }

    fun save() {
        val nameError = if (form.fullName.isBlank()) "Enter the player's name" else null
        val positionError = if (form.position.isBlank()) "Pick a position" else null
        if (nameError != null || positionError != null) {
            form = form.copy(nameError = nameError, positionError = positionError)
            return
        }
        onSave(
            SquadPlayer(
                id = java.util.UUID.randomUUID().toString(),
                fullName = form.fullName.trim(),
                position = form.position,
                status = form.status
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Add Player", style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary) },
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
            item {
                SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        IconBubble(size = 84.dp, background = FormUpColors.PrimaryTint) {
                            Icon(Icons.Outlined.AddAPhoto, contentDescription = "Upload photo", tint = FormUpColors.TextSecondary, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Upload Photo", fontFamily = Mono, fontSize = 11.sp, color = FormUpColors.TextSecondary)
                    }

                    Spacer(Modifier.height(18.dp))
                    FieldLabel("Full Name")
                    FormField(
                        value = form.fullName,
                        onValueChange = { update { s -> s.copy(fullName = it, nameError = null) } },
                        placeholder = "Enter player name",
                        error = form.nameError
                    )

                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            FieldLabel("Date of Birth")
                            FormField(
                                value = form.dateOfBirth,
                                onValueChange = { update { s -> s.copy(dateOfBirth = it) } },
                                placeholder = "mm/dd/yyyy"
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            FieldLabel("Position")
                            PositionDropdown(
                                selected = form.position,
                                onSelect = { update { s -> s.copy(position = it, positionError = null) } },
                                error = form.positionError
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    FieldLabel("Jersey Number")
                    FormField(
                        value = form.jerseyNumber,
                        onValueChange = { update { s -> s.copy(jerseyNumber = it.filter(Char::isDigit).take(2)) } },
                        placeholder = "##"
                    )
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
                    Text("Contact Details", style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary)
                    Spacer(Modifier.height(16.dp))

                    FieldLabel("Email Address")
                    FormField(
                        value = form.email,
                        onValueChange = { update { s -> s.copy(email = it) } },
                        placeholder = "player@example.com"
                    )

                    Spacer(Modifier.height(14.dp))
                    FieldLabel("Phone Number")
                    FormField(
                        value = form.phoneNumber,
                        onValueChange = { update { s -> s.copy(phoneNumber = it) } },
                        placeholder = "+27 82 000 0000"
                    )

                    Spacer(Modifier.height(14.dp))
                    FieldLabel("Parent/Guardian Name (Optional)")
                    FormField(
                        value = form.guardianName,
                        onValueChange = { update { s -> s.copy(guardianName = it) } },
                        placeholder = "Enter parent name"
                    )
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
                    Text("Health & Status", style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary)
                    Spacer(Modifier.height(16.dp))
                    FieldLabel("Injury Status")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PlayerStatus.entries.forEach { status ->
                            StatusToggleChip(
                                label = status.name.lowercase().replaceFirstChar { it.uppercase() },
                                selected = form.status == status,
                                onClick = { update { s -> s.copy(status = status) } },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = ::save,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FormUpColors.Primary, contentColor = FormUpColors.Surface),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Add Player to Roster", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontFamily = Mono, fontSize = 11.sp, color = FormUpColors.TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
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
            Text(it, fontFamily = Mono, fontSize = 10.sp, color = FormUpColors.Danger, modifier = Modifier.padding(top = 4.dp, start = 2.dp))
        }
    }
}

@Composable
private fun PositionDropdown(selected: String, onSelect: (String) -> Unit, error: String?) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(FormUpColors.Surface, RoundedCornerShape(10.dp))
                .border(1.dp, if (error != null) FormUpColors.Danger else FormUpColors.Hairline, RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                selected.ifBlank { "Select Position" },
                color = if (selected.isBlank()) FormUpColors.TextSecondary else FormUpColors.TextPrimary
            )
            Spacer(Modifier.weight(1f))
            Icon(Icons.Outlined.ExpandMore, contentDescription = null, tint = FormUpColors.TextPrimary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            PositionOptions.forEach { position ->
                DropdownMenuItem(text = { Text(position) }, onClick = { onSelect(position); expanded = false })
            }
        }
        error?.let {
            Text(it, fontFamily = Mono, fontSize = 10.sp, color = FormUpColors.Danger, modifier = Modifier.padding(top = 4.dp, start = 2.dp))
        }
    }
}

@Composable
private fun StatusToggleChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(46.dp)
            .background(if (selected) FormUpColors.Mint else FormUpColors.Surface, RoundedCornerShape(23.dp))
            .border(1.dp, if (selected) FormUpColors.Mint else FormUpColors.Hairline, RoundedCornerShape(23.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) FormUpColors.PrimaryDeep else FormUpColors.TextPrimary)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun AddPlayerScreenPreview() {
    FormUpTheme { AddPlayerScreen() }
}
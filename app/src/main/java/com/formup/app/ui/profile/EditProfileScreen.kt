package com.formup.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.common.BackHeader
import com.formup.app.ui.home.components.FormUpBottomBar
import com.formup.app.ui.home.components.FormUpTopBar
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.Mono

// Form for editing coach profile details and team metadata
@Composable
fun EditProfileScreen(
    coachName: String,
    coachRole: String,
    teamName: String,
    squad: String,
    season: String,
    homeGround: String,
    hasUnread: Boolean,
    onBack: () -> Unit,
    onNotifications: () -> Unit,
    onSave: (
        coachName: String,
        coachRole: String,
        teamName: String,
        squad: String,
        season: String,
        homeGround: String
    ) -> Unit,
    onSelectTab: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local state pre-filled with current values
    var name by remember { mutableStateOf(coachName) }
    var role by remember { mutableStateOf(coachRole) }
    var team by remember { mutableStateOf(teamName) }
    var squadName by remember { mutableStateOf(squad) }
    var seasonLabel by remember { mutableStateOf(season) }
    var ground by remember { mutableStateOf(homeGround) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = { FormUpTopBar(hasUnread = hasUnread, onNotifications = onNotifications) },
        bottomBar = { FormUpBottomBar(selected = HomeTab.Profile, onSelect = onSelectTab) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            BackHeader(title = "Edit Profile", onBack = onBack)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Coach info
                item {
                    SectionCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Coach",
                            style = MaterialTheme.typography.titleMedium,
                            color = FormUpColors.TextPrimary
                        )
                        Spacer(Modifier.height(10.dp))
                        FormField("Full name", name) { name = it }
                        Spacer(Modifier.height(10.dp))
                        FormField("Role", role) { role = it }
                    }
                }

                // Team info
                item {
                    SectionCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Team Information",
                            style = MaterialTheme.typography.titleMedium,
                            color = FormUpColors.TextPrimary
                        )
                        Spacer(Modifier.height(10.dp))
                        FormField("Team name", team) { team = it }
                        Spacer(Modifier.height(10.dp))
                        FormField("Squad", squadName) { squadName = it }
                        Spacer(Modifier.height(10.dp))
                        FormField("Season", seasonLabel) { seasonLabel = it }
                        Spacer(Modifier.height(10.dp))
                        FormField("Home ground", ground) { ground = it }
                    }
                }

                // Action buttons
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, FormUpColors.Hairline),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FormUpColors.TextPrimary)
                        ) {
                            Text("Cancel", fontFamily = Mono, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = { onSave(name, role, team, squadName, seasonLabel, ground) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FormUpColors.PrimaryDeep,
                                contentColor = FormUpColors.Surface
                            )
                        ) {
                            Text("Save", fontFamily = Mono, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

// Reusable text input field styled for FormUp forms
@Composable
private fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontFamily = Mono,
                fontSize = 11.sp,
                color = FormUpColors.TextSecondary
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FormUpColors.Primary,
            unfocusedBorderColor = FormUpColors.Hairline,
            focusedTextColor = FormUpColors.TextPrimary,
            unfocusedTextColor = FormUpColors.TextPrimary,
            cursorColor = FormUpColors.Primary,
            focusedContainerColor = FormUpColors.Surface,
            unfocusedContainerColor = FormUpColors.Surface
        )
    )
}

package com.formup.app.ui.team

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.FormUpBottomBar
import com.formup.app.ui.home.components.FormUpTopBar
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.team.components.PlayerRow
import com.formup.app.ui.team.components.SquadOverviewCard
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme

@Composable
fun TeamScreen(
    state: TeamUiState = SampleTeamState,
    onNotifications: () -> Unit = {},
    onAddPlayer: () -> Unit = {},
    onExport: () -> Unit = {},
    onSelectTab: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }

    val visiblePlayers = state.players.filter {
        query.isBlank() ||
                it.fullName.contains(
                    query,
                    ignoreCase = true
                )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            FormUpTopBar(
                hasUnread = false,
                onNotifications = onNotifications
            )
        },
        bottomBar = {
            FormUpBottomBar(
                selected = HomeTab.Team,
                onSelect = onSelectTab
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            "Search players...",
                            color = FormUpColors.TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            tint = FormUpColors.TextSecondary
                        )
                    },
                    singleLine = true,
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

            item {
                SquadOverviewCard(
                    total = state.total
                )
            }

            item {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 12.dp,
                            bottom = 2.dp
                        ),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Player Roster",
                        style = MaterialTheme.typography.titleLarge,
                        color = FormUpColors.TextPrimary
                    )

                    Text(
                        "${state.total} PLAYERS",
                        fontFamily = com.formup.app.ui.theme.Mono,
                        fontSize = 11.sp,
                        color = FormUpColors.TextSecondary
                    )
                }
            }

            if (visiblePlayers.isEmpty()) {
                item {
                    SectionCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No players match this search",
                                style = MaterialTheme.typography.titleMedium,
                                color = FormUpColors.TextPrimary
                            )
                        }
                    }
                }
            } else {
                items(
                    visiblePlayers,
                    key = { it.id }
                ) { player ->
                    PlayerRow(player)
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
            }

            item {
                Button(
                    onClick = onAddPlayer,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FormUpColors.Primary,
                        contentColor = FormUpColors.Surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        Icons.Outlined.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        "Add Player",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = onExport,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        FormUpColors.Hairline
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = FormUpColors.Surface,
                        contentColor = FormUpColors.TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        Icons.Outlined.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        "Export Squad Data",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun TeamScreenPreview() {
    FormUpTheme {
        TeamScreen()
    }
}
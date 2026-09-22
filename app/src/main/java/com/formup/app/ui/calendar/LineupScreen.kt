package com.formup.app.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.formup.app.data.AvailabilityStatus
import com.formup.app.data.Player
import com.formup.app.ui.theme.FormUpColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineupScreen(
    opponent: String,
    players: List<Player>,
    selectedCount: Int,
    onBack: () -> Unit,
    onTogglePlayer: (String) -> Unit,
    onClear: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lineup vs $opponent",
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = FormUpColors.Surface,
                    border = BorderStroke(
                        1.dp,
                        FormUpColors.Hairline
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Starting XI",
                            style = MaterialTheme.typography.titleLarge,
                            color = FormUpColors.TextPrimary
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "$selectedCount / 11 selected",
                            color = FormUpColors.TextSecondary
                        )
                    }
                }
            }

            items(
                items = players,
                key = { it.id }
            ) { player ->

                val unavailable =
                    player.status == AvailabilityStatus.Out

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = !unavailable
                        ) {
                            onTogglePlayer(player.id)
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = FormUpColors.Surface,
                    border = BorderStroke(
                        1.dp,
                        if (player.inLineup) {
                            FormUpColors.Primary
                        } else {
                            FormUpColors.Hairline
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = player.name,
                                fontWeight = FontWeight.SemiBold,
                                color = FormUpColors.TextPrimary
                            )

                            Text(
                                text = "${player.position} • ${player.status.label}",
                                color = FormUpColors.TextSecondary
                            )
                        }

                        Icon(
                            imageVector =
                                if (player.inLineup) {
                                    Icons.Filled.CheckCircle
                                } else {
                                    Icons.Filled.RadioButtonUnchecked
                                },
                            contentDescription = null,
                            tint =
                                if (player.inLineup) {
                                    FormUpColors.Primary
                                } else {
                                    FormUpColors.TextSecondary
                                },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onClear,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }

                    Button(
                        onClick = onSave,
                        enabled = selectedCount > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FormUpColors.Primary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Lineup")
                    }
                }
            }
        }
    }
}
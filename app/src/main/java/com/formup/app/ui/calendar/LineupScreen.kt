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
import androidx.compose.ui.unit.sp
import com.formup.app.data.Player
import com.formup.app.ui.theme.FormUpColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineupScreen(
    opponent: String,
    players: List<Player>,
    formation: String,
    startingCount: Int,
    substituteCount: Int,
    selectionForPlayer: (String) -> String,
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
                            text = "Formation",
                            style = MaterialTheme.typography.labelLarge,
                            color = FormUpColors.TextSecondary
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = formation,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = FormUpColors.Primary
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "$startingCount / 11 Starting • $substituteCount Substitute",
                            color = FormUpColors.TextSecondary
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Squad",
                    style = MaterialTheme.typography.titleLarge,
                    color = FormUpColors.TextPrimary
                )

                Text(
                    text = "Tap a player to cycle between Starting, Substitute and Not Selected.",
                    fontSize = 13.sp,
                    color = FormUpColors.TextSecondary
                )
            }

            items(
                items = players,
                key = { it.id }
            ) { player ->

                val selection =
                    selectionForPlayer(player.id)

                val isStarting =
                    selection == "Starting"

                val isSubstitute =
                    selection == "Substitute"

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTogglePlayer(player.id)
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = FormUpColors.Surface,
                    border = BorderStroke(
                        1.dp,
                        if (selection != "NotSelected") {
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
                                text = player.position,
                                color = FormUpColors.TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        when {
                            isStarting -> {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = "Starting",
                                    tint = FormUpColors.Primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            isSubstitute -> {
                                Text(
                                    text = "SUB",
                                    fontWeight = FontWeight.Bold,
                                    color = FormUpColors.Primary,
                                    fontSize = 12.sp
                                )
                            }

                            else -> {
                                Icon(
                                    Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = "Not selected",
                                    tint = FormUpColors.TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
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
                        enabled = startingCount == 11,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FormUpColors.Primary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (startingCount == 11)
                                "Save Lineup"
                            else
                                "Select ${11 - startingCount} More"
                        )
                    }
                }
            }
        }
    }
}
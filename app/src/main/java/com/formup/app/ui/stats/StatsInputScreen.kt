package com.formup.app.ui.stats

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.FormUpBottomBar
import com.formup.app.ui.home.components.FormUpTopBar
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.home.components.StatusPill
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

@Composable
fun StatsInputScreen(
    state: StatsInputUiState = SampleStatsInputState,
    onBack: () -> Unit = {},
    onCustomizeColumns: () -> Unit = {},
    onCancel: () -> Unit = {},
    onSave: (List<PlayerStatEntry>) -> Unit = {},
    onNotifications: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val playerRows = remember {
        mutableStateListOf<PlayerStatEntry>().apply { addAll(state.players) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            FormUpTopBar(hasUnread = false, onNotifications = onNotifications)
        },
        bottomBar = {
            FormUpBottomBar(selected = HomeTab.Stats, onSelect = {})
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Back",
                        tint = FormUpColors.TextPrimary
                    )
                }
                Text(
                    text = "Stats Input",
                    style = MaterialTheme.typography.titleMedium,
                    color = FormUpColors.TextSecondary
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusPill(text = state.resultBadge)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = state.matchDate,
                                fontFamily = Mono,
                                fontSize = 12.sp,
                                color = FormUpColors.TextSecondary
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = state.opponent,
                            style = MaterialTheme.typography.titleLarge,
                            color = FormUpColors.TextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = state.competition,
                            style = MaterialTheme.typography.bodySmall,
                            color = FormUpColors.TextSecondary
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ScoreField(label = state.homeLabel, score = state.homeScore, modifier = Modifier.weight(1f))
                        Text(
                            text = ":",
                            style = MaterialTheme.typography.titleLarge,
                            color = FormUpColors.TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        ScoreField(label = state.awayLabel, score = state.awayScore, modifier = Modifier.weight(1f))
                    }
                }

                item {
                    OutlinedButton(
                        onClick = onCustomizeColumns,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, FormUpColors.Primary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FormUpColors.Primary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(text = "Customize Columns", fontFamily = Mono, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }

                item {
                    SectionCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(FormUpColors.PrimaryTint)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Player",
                                fontFamily = Mono,
                                fontSize = 11.sp,
                                color = FormUpColors.TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Goals",
                                fontFamily = Mono,
                                fontSize = 11.sp,
                                color = FormUpColors.TextSecondary,
                                modifier = Modifier.width(96.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Text(
                                text = "Assists",
                                fontFamily = Mono,
                                fontSize = 11.sp,
                                color = FormUpColors.TextSecondary,
                                modifier = Modifier.width(96.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        playerRows.forEachIndexed { index, player ->
                            HorizontalDivider(color = FormUpColors.Hairline)
                            PlayerStatRow(
                                player = player,
                                onGoalsChange = { delta ->
                                    playerRows[index] = player.copy(goals = (player.goals + delta).coerceAtLeast(0))
                                },
                                onAssistsChange = { delta ->
                                    playerRows[index] = player.copy(assists = (player.assists + delta).coerceAtLeast(0))
                                }
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, FormUpColors.Hairline),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FormUpColors.TextPrimary)
                        ) {
                            Text(text = "Cancel", fontFamily = Mono, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = { onSave(playerRows.toList()) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FormUpColors.PrimaryDeep,
                                contentColor = FormUpColors.Surface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.size(6.dp))
                            Text(text = "Save Statistics", fontFamily = Mono, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreField(label: String, score: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontFamily = Mono,
            fontSize = 10.sp,
            color = FormUpColors.TextSecondary
        )
        Spacer(Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = FormUpColors.Surface,
            border = BorderStroke(1.dp, FormUpColors.Hairline)
        ) {
            Text(
                text = score.toString(),
                fontFamily = Mono,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = FormUpColors.TextPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun PlayerStatRow(
    player: PlayerStatEntry,
    onGoalsChange: (Int) -> Unit,
    onAssistsChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(player.badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.number.toString(),
                    fontFamily = Mono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = player.badgeTextColor
                )
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = FormUpColors.TextPrimary
                )
                Text(
                    text = player.position,
                    style = MaterialTheme.typography.bodySmall,
                    color = FormUpColors.TextSecondary
                )
            }
        }

        Stepper(value = player.goals, onChange = onGoalsChange, modifier = Modifier.width(96.dp))
        Stepper(value = player.assists, onChange = onAssistsChange, modifier = Modifier.width(96.dp))
    }
}

@Composable
private fun Stepper(value: Int, onChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperButton(icon = Icons.Filled.Remove, onClick = { onChange(-1) })
        Text(
            text = value.toString(),
            fontFamily = Mono,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = FormUpColors.TextPrimary,
            modifier = Modifier.width(22.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        StepperButton(icon = Icons.Filled.Add, onClick = { onChange(1) })
    }
}

@Composable
private fun StepperButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(FormUpColors.NavIndicator)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FormUpColors.TextSecondary,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun StatsInputScreenPreview() {
    FormUpTheme {
        StatsInputScreen()
    }
}
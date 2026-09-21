package com.formup.app.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.IconBubble
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.home.components.StatusPill
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailsScreen(
    state: MatchDetailsUiState = SampleMatchDetailsState,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            TopAppBar(
                title = {},
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    StatusPill(text = state.competitionTag, background = FormUpColors.AmberTint, contentColor = FormUpColors.AmberIcon)
                    Spacer(Modifier.height(6.dp))
                    Text(state.title, style = MaterialTheme.typography.headlineMedium, color = FormUpColors.Primary)
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth()) {
                    InfoBlock(Icons.Filled.CalendarMonth, "Date & Time", state.dateLabel, state.timeLabel)
                    Spacer(Modifier.height(14.dp))
                    InfoBlock(Icons.Filled.LocationOn, "Location", state.venueName, state.venueDetail)
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = FormUpColors.Surface,
                    border = BorderStroke(1.5.dp, FormUpColors.Primary)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Squad Availability", style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary)
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(state.attending.toString(), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = FormUpColors.Primary)
                            Text("/${state.squadSize}", fontSize = 18.sp, color = FormUpColors.TextSecondary)
                            Spacer(Modifier.weight(1f))
                            Text("Attending", fontFamily = Mono, fontSize = 11.sp, color = FormUpColors.TextSecondary)
                        }
                        Spacer(Modifier.height(8.dp))
                        SquadAvailabilityBar(inCount = state.inCount, tbdCount = state.tbdCount, outCount = state.outCount)
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AvailabilityStat("IN", state.inCount, FormUpColors.Primary, Modifier.weight(1f))
                            AvailabilityStat("OUT", state.outCount, FormUpColors.Danger, Modifier.weight(1f))
                            AvailabilityStat("TBD", state.tbdCount, FormUpColors.AmberIcon, Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Starting XI", style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary, modifier = Modifier.weight(1f))
                        StatusPill(text = state.formation, background = FormUpColors.NavIndicator, contentColor = FormUpColors.TextSecondary)
                    }
                    Spacer(Modifier.height(12.dp))
                    PitchView(players = state.startingXi)
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 4.dp, horizontal = 4.dp)) {
                    Text("Substitutes", style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary, modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp))
                    state.substitutes.forEachIndexed { index, sub ->
                        if (index > 0) HorizontalDivider(color = FormUpColors.Hairline, modifier = Modifier.padding(horizontal = 14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(sub.number.toString(), fontFamily = Mono, fontSize = 13.sp, color = FormUpColors.TextSecondary, modifier = Modifier.width(28.dp))
                            Text(sub.name, style = MaterialTheme.typography.titleMedium, color = FormUpColors.TextPrimary, modifier = Modifier.weight(1f))
                            Text(sub.position, fontFamily = Mono, fontSize = 12.sp, color = FormUpColors.TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBlock(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, primary: String, secondary: String) {
    Row(verticalAlignment = Alignment.Top) {
        IconBubble(size = 36.dp, background = FormUpColors.PrimaryTint) {
            Icon(icon, contentDescription = null, tint = FormUpColors.Primary, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.size(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.titleMedium, color = FormUpColors.TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(primary, style = MaterialTheme.typography.bodyMedium, color = FormUpColors.TextSecondary)
            Text(secondary, style = MaterialTheme.typography.bodyMedium, color = FormUpColors.TextSecondary)
        }
    }
}

@Composable
private fun SquadAvailabilityBar(inCount: Int, tbdCount: Int, outCount: Int) {
    val total = (inCount + tbdCount + outCount).coerceAtLeast(1)
    Row(
        Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(FormUpColors.Hairline)
    ) {
        Box(Modifier.weight(inCount.toFloat().coerceAtLeast(0.0001f)).fillMaxSize().background(FormUpColors.Primary))
        Box(Modifier.weight(tbdCount.toFloat().coerceAtLeast(0.0001f)).fillMaxSize().background(FormUpColors.AmberIcon))
        Box(Modifier.weight(outCount.toFloat().coerceAtLeast(0.0001f)).fillMaxSize().background(FormUpColors.Hairline))
    }
}

@Composable
private fun AvailabilityStat(label: String, value: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(10.dp), color = FormUpColors.PrimaryTint.copy(alpha = 0.4f)) {
        Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontFamily = Mono, fontSize = 10.sp, letterSpacing = 0.5.sp, color = color)
            Spacer(Modifier.height(2.dp))
            Text(value.toString(), style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary)
        }
    }
}

@Composable
private fun PitchView(players: List<LineupPlayer>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(12.dp))
            .background(FormUpColors.Primary)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineColor = Color.White.copy(alpha = 0.45f)
            drawLine(lineColor, Offset(0f, size.height / 2f), Offset(size.width, size.height / 2f), strokeWidth = 2f)
            drawCircle(lineColor, radius = size.minDimension * 0.14f, center = Offset(size.width / 2f, size.height / 2f), style = Stroke(width = 2f))
            val boxWidth = size.width * 0.5f
            val boxHeight = size.height * 0.14f
            drawRect(
                color = lineColor,
                topLeft = Offset((size.width - boxWidth) / 2f, size.height - boxHeight),
                size = Size(boxWidth, boxHeight),
                style = Stroke(width = 2f)
            )
        }

        players.forEach { player ->
            Column(
                modifier = Modifier.align(
                    BiasAlignment(
                        horizontalBias = player.xPercent * 2f - 1f,
                        verticalBias = player.yPercent * 2f - 1f
                    )
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (player.isGoalkeeper) FormUpColors.Amber else FormUpColors.Surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(player.number.toString(), fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FormUpColors.TextPrimary)
                }
                Spacer(Modifier.height(3.dp))
                Surface(shape = RoundedCornerShape(4.dp), color = FormUpColors.TextPrimary.copy(alpha = 0.85f)) {
                    Text(
                        player.name,
                        fontFamily = Mono,
                        fontSize = 9.sp,
                        color = FormUpColors.Surface,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1600)
@Composable
private fun MatchDetailsScreenPreview() {
    FormUpTheme { MatchDetailsScreen() }
}
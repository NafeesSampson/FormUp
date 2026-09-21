package com.formup.app.ui.team.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.IconBubble
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.home.components.StatusPill
import com.formup.app.ui.team.PlayerStatus
import com.formup.app.ui.team.SquadPlayer
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.Mono

@Composable
fun SquadOverviewCard(
    total: Int,
    fit: Int,
    doubt: Int,
    out: Int,
    modifier: Modifier = Modifier
) {
    SectionCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
        Text("Squad Overview", style = MaterialTheme.typography.titleLarge, color = FormUpColors.TextPrimary)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            StatCell("TOTAL", total, FormUpColors.Primary, Modifier.weight(1f))
            StatCell("FIT", fit, FormUpColors.TextPrimary, Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth()) {
            StatCell("DOUBT", doubt, FormUpColors.TextPrimary, Modifier.weight(1f))
            StatCell("OUT", out, FormUpColors.TextPrimary, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCell(label: String, value: Int, valueColor: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, fontFamily = Mono, fontSize = 11.sp, letterSpacing = 0.4.sp, color = FormUpColors.TextSecondary)
        Text(value.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

/** Small colored badge for a player's status, built on the shared StatusPill. */
@Composable
fun PlayerStatusPill(status: PlayerStatus) {
    val (label, background, content) = when (status) {
        PlayerStatus.FIT -> Triple("FIT", FormUpColors.Mint, FormUpColors.PrimaryDeep)
        PlayerStatus.DOUBT -> Triple("DOUBT", FormUpColors.AmberTint, FormUpColors.AmberIcon)
        PlayerStatus.OUT -> Triple("OUT", FormUpColors.Danger.copy(alpha = 0.12f), FormUpColors.Danger)
    }
    StatusPill(text = label, background = background, contentColor = content)
}

@Composable
fun PlayerRow(player: SquadPlayer, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    SectionCard(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconBubble(size = 44.dp, background = FormUpColors.PrimaryTint) {
                Text(
                    text = initialsOf(player.fullName),
                    color = FormUpColors.PrimaryDeep,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(player.fullName, style = MaterialTheme.typography.titleMedium, color = FormUpColors.TextPrimary)
                Text(player.position, fontFamily = Mono, fontSize = 11.sp, color = FormUpColors.TextSecondary)
            }
            PlayerStatusPill(player.status)
        }
    }
}

private fun initialsOf(name: String): String =
    name.trim().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }
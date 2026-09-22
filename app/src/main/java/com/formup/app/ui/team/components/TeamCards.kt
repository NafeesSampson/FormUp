package com.formup.app.ui.team.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.formup.app.ui.team.SquadPlayer
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.Mono

@Composable
fun SquadOverviewCard(
    total: Int,
    modifier: Modifier = Modifier
) {
    SectionCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(
            "Squad Overview",
            style = MaterialTheme.typography.titleLarge,
            color = FormUpColors.TextPrimary
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "TOTAL PLAYERS",
            fontFamily = Mono,
            fontSize = 11.sp,
            letterSpacing = 0.4.sp,
            color = FormUpColors.TextSecondary
        )

        Text(
            total.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = FormUpColors.Primary
        )
    }
}

@Composable
fun PlayerRow(
    player: SquadPlayer,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    SectionCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = 14.dp,
            vertical = 12.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(
                size = 44.dp,
                background = FormUpColors.PrimaryTint
            ) {
                Text(
                    text = initialsOf(player.fullName),
                    color = FormUpColors.PrimaryDeep,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    player.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = FormUpColors.TextPrimary
                )

                Text(
                    player.position,
                    fontFamily = Mono,
                    fontSize = 11.sp,
                    color = FormUpColors.TextSecondary
                )
            }
        }
    }
}

private fun initialsOf(name: String): String =
    name.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }
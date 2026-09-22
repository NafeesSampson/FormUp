package com.formup.app.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.LastMatch
import com.formup.app.ui.theme.FormUpColors

@Composable
fun LastMatchCard(
    match: LastMatch,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    SectionCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Last Match",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelMedium,
                color = FormUpColors.Primary,
                modifier = Modifier.clickable(onClick = onViewAll)
            )
        }

        Spacer(Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = FormUpColors.PrimaryTint
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamScore(
                    score = match.homeScore,
                    team = match.homeTeam,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = match.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = FormUpColors.TextSecondary
                )
                TeamScore(
                    score = match.awayScore,
                    team = match.awayTeam,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        match.stats.forEach { (label, value) ->
            HorizontalDivider(color = FormUpColors.Hairline)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FormUpColors.TextPrimary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = FormUpColors.TextPrimary
                )
            }
        }
    }
}

@Composable
private fun TeamScore(score: Int, team: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = score.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = FormUpColors.TextPrimary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = team,
            style = MaterialTheme.typography.labelSmall,
            color = FormUpColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

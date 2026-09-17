package com.formup.app.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.NextMatch
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.Mono

@Composable
fun NextMatchCard(
    match: NextMatch,
    onMatchDetails: () -> Unit,
    onLineup: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = FormUpColors.Surface,
        border = BorderStroke(1.dp, FormUpColors.Hairline)
    ) {
        Column(Modifier.clip(RoundedCornerShape(14.dp))) {
            // Accent strip along the top edge of the card.
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(FormUpColors.Primary)
            )

            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(Modifier.weight(1f)) {
                        StatusPill("NEXT MATCH")
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = match.opponent,
                            style = MaterialTheme.typography.titleLarge,
                            color = FormUpColors.TextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = match.date,
                            color = FormUpColors.Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = match.kickoff,
                            style = MaterialTheme.typography.labelMedium,
                            color = FormUpColors.TextSecondary
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = FormUpColors.TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = match.venue,
                        style = MaterialTheme.typography.bodySmall,
                        color = FormUpColors.TextSecondary
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onMatchDetails,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FormUpColors.Primary,
                            contentColor = FormUpColors.Surface
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Match Details",
                            fontFamily = Mono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    OutlinedButton(
                        onClick = onLineup,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.5.dp, FormUpColors.Primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = FormUpColors.Primary
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Lineup",
                            fontFamily = Mono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

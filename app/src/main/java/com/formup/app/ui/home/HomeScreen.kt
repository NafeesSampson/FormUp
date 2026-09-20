package com.formup.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.AvailabilityCard
import com.formup.app.ui.home.components.FormUpBottomBar
import com.formup.app.ui.home.components.FormUpTopBar
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.home.components.LastMatchCard
import com.formup.app.ui.home.components.NextMatchCard
import com.formup.app.ui.home.components.QuickActionsRow
import com.formup.app.ui.home.components.UpdateCard
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

@Composable
fun HomeScreen(
    state: HomeUiState = SampleHomeState,
    onNotifications: () -> Unit = {},
    onInvitePlayer: () -> Unit = {},
    onStats: () -> Unit = {},
    onSelectTab: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background,
        topBar = {
            FormUpTopBar(
                hasUnread = state.unreadNotifications,
                onNotifications = onNotifications
            )
        },
        bottomBar = {
            FormUpBottomBar(
                selected = HomeTab.Home,
                onSelect = onSelectTab
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 18.dp,
                bottom = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Greeting(
                    name = state.coachName,
                    prompt = state.prompt
                )
            }

            item {
                NextMatchCard(
                    match = state.nextMatch,
                    onMatchDetails = { /* TODO: navigate to match details */ },
                    onLineup = { /* TODO: navigate to lineup */ }
                )
            }

            item {
                QuickActionsRow(
                    onAttendance = { /* TODO */ },
                    onSelection = { /* TODO */ },
                    onStats = onStats
                )
            }

            item {
                AvailabilityCard(
                    availability = state.availability,
                    onInfo = { /* TODO: show availability info */ }
                )
            }

            item {
                LastMatchCard(
                    match = state.lastMatch,
                    onViewAll = { /* TODO: navigate to results */ }
                )
            }

            item {
                Text(
                    text = "Recent Updates",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(state.updates) { update ->
                UpdateCard(update)
            }

            item {
                InvitePlayerButton(
                    onClick = onInvitePlayer,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun Greeting(
    name: String,
    prompt: String
) {
    androidx.compose.foundation.layout.Column {
        Text(
            text = "Welcome back, $name",
            style = MaterialTheme.typography.headlineMedium,
            color = FormUpColors.TextPrimary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = prompt,
            style = MaterialTheme.typography.bodyMedium,
            color = FormUpColors.TextSecondary
        )
    }
}

@Composable
private fun InvitePlayerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FormUpColors.Primary,
            contentColor = FormUpColors.Surface
        )
    ) {
        Icon(
            imageVector = Icons.Filled.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )

        Spacer(Modifier.size(8.dp))

        Text(
            text = "Invite Player",
            fontFamily = Mono,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1280)
@Composable
private fun HomeScreenPreview() {
    FormUpTheme {
        HomeScreen()
    }
}
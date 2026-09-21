package com.formup.app.ui.invite

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.home.components.FormUpBottomBar
import com.formup.app.ui.home.components.FormUpTopBar
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.home.components.SectionCard
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import com.formup.app.ui.theme.Mono

data class InvitePlayerUiState(
    val inviteCode: String,
    val directLink: String
)

val SampleInviteState = InvitePlayerUiState(
    inviteCode = "FORMUP-9X2J",
    directLink = "formup.app/join/9x2j"
)

@Composable
fun InvitePlayerScreen(
    state: InvitePlayerUiState = SampleInviteState,
    onCopyCode: () -> Unit = {},
    onShareLink: () -> Unit = {},
    onDownloadQr: () -> Unit = {},
    onDone: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onSelectTab: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                selected = HomeTab.Home,
                onSelect = onSelectTab
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Invite Players",
                        style = MaterialTheme.typography.headlineMedium,
                        color = FormUpColors.TextPrimary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Share these details with players so they can join your team roster securely.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FormUpColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Team Invite Code",
                        style = MaterialTheme.typography.titleMedium,
                        color = FormUpColors.TextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Players can enter this code during app sign-up.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FormUpColors.TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = FormUpColors.PrimaryTint
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.inviteCode,
                                fontFamily = Mono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = FormUpColors.PrimaryDeep,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copy invite code",
                                tint = FormUpColors.Primary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable(onClick = onCopyCode)
                            )
                        }
                    }
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Direct Link",
                        style = MaterialTheme.typography.titleMedium,
                        color = FormUpColors.TextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Send a direct join link via messaging apps or email.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FormUpColors.TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = FormUpColors.Surface,
                        border = BorderStroke(1.dp, FormUpColors.Hairline)
                    ) {
                        Text(
                            text = state.directLink,
                            fontFamily = Mono,
                            fontSize = 13.sp,
                            color = FormUpColors.TextSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 13.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onShareLink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FormUpColors.Primary,
                            contentColor = FormUpColors.Surface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.IosShare,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = "Share Link",
                            fontFamily = Mono,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                SectionCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "In-Person Scan",
                            style = MaterialTheme.typography.titleMedium,
                            color = FormUpColors.TextPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Scan with camera app",
                            style = MaterialTheme.typography.bodySmall,
                            color = FormUpColors.TextSecondary
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .background(FormUpColors.PrimaryTint, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCode2,
                                contentDescription = "Team QR code",
                                tint = FormUpColors.Primary,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onDownloadQr)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FileDownload,
                                contentDescription = null,
                                tint = FormUpColors.Primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.size(6.dp))
                            Text(
                                text = "Download QR",
                                fontFamily = Mono,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = FormUpColors.Primary
                            )
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, FormUpColors.Primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FormUpColors.Primary
                    )
                ) {
                    Text(
                        text = "Done",
                        fontFamily = Mono,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 900)
@Composable
private fun InvitePlayerScreenPreview() {
    FormUpTheme {
        InvitePlayerScreen()
    }
}

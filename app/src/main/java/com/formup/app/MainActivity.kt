package com.formup.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.viewmodel.compose.viewModel
import com.formup.app.ui.FormUpViewModel
import com.formup.app.ui.auth.AccountRole
import com.formup.app.ui.auth.LoginScreen
import com.formup.app.ui.auth.SignUpScreen
import com.formup.app.ui.auth.NewAccount
import com.google.firebase.auth.FirebaseAuth
import com.formup.app.ui.home.HomeScreen
import com.formup.app.ui.home.components.FormUpBottomBar
import com.formup.app.ui.home.components.FormUpTopBar
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.invite.InvitePlayerScreen
import com.formup.app.ui.invite.InvitePlayerUiState
import androidx.compose.ui.unit.dp
import com.formup.app.ui.navigation.Destination
import com.formup.app.ui.notifications.NotificationsScreen
import com.formup.app.ui.profile.EditProfileScreen
import com.formup.app.ui.profile.ProfileScreen
import com.formup.app.ui.stats.MatchReportScreen
import com.formup.app.ui.stats.StatsInputScreen
import com.formup.app.ui.stats.StatsScreen
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.exceptions.GetCredentialCancellationException
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import com.formup.app.ui.auth.CreateTeamScreen
import emailSignIn
import emailSignUp
import googleSignIn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormUpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FormUpColors.Background
                ) {
                    FormUpApp()
                }
            }
        }
    }
}

@Composable
private fun FormUpApp(viewModel: FormUpViewModel = viewModel()) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()
    var signedIn by remember { mutableStateOf(auth.currentUser != null) }
    var showSignUp by remember { mutableStateOf(false) }
    var needsTeam by remember { mutableStateOf(false) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                signedIn = false
                needsTeam = false
                showSignUp = false
            }
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    fun completeSignIn(block: suspend () -> Boolean) {
        scope.launch {
            try {
                val hasTeam = block()          // true = coach already has a team
                needsTeam = !hasTeam
                signedIn = true
                if (hasTeam) viewModel.refreshFromApi()
            } catch (e: CancellationException) {
                throw e
            } catch (e: GetCredentialCancellationException) {
                // user closed the Google picker, do nothing
            } catch (e: Exception) {
                Log.e("Auth", "Sign-in failed", e)
                Toast.makeText(context, e.message ?: "Sign-in failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    if (!signedIn) {
        if (showSignUp) {
            SignUpScreen(
                onCreateAccount = { account: NewAccount ->
                    if (account.role != AccountRole.COACH) {
                        Toast.makeText(context, "FormUp is coach-only.", Toast.LENGTH_SHORT).show()
                    } else {
                        completeSignIn { emailSignUp(account.fullName, account.email, account.password) }
                    }
                },
                onNavigateToLogin = { showSignUp = false },
                onGoogleSignUp = { completeSignIn { googleSignIn(context) } }
            )
        } else {
            LoginScreen(
                onSignIn = { email, password, role ->
                    if (role != AccountRole.COACH) {
                        Toast.makeText(context, "FormUp is coach-only.", Toast.LENGTH_SHORT).show()
                    } else {
                        completeSignIn { emailSignIn(email, password) }
                    }
                },
                onForgotPassword = {
                    Toast.makeText(context, "Enter your email first, then use password reset from Firebase.", Toast.LENGTH_LONG).show()
                },
                onGoogleSignIn = { completeSignIn { googleSignIn(context) } },
                onNavigateToSignUp = { showSignUp = true }
            )
        }
        return
    }

    if (needsTeam) {
        CreateTeamScreen(
            onBack = { auth.signOut() },
            onCancel = { auth.signOut() },
            onTeamCreated = {
                // TODO: send team.teamName / team.ageGroupLevel to PUT /api/team
                needsTeam = false
                viewModel.refreshFromApi()
            }
        )
        return
    }

    // One-shot feedback from the ViewModel.
    val message = viewModel.message
    LaunchedEffect(message) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.consumeMessage()
        }
    }

    val activity = context as? ComponentActivity
    BackHandler(enabled = true) {
        if (!viewModel.back()) activity?.finish()
    }

    val hasUnread = viewModel.unreadCount > 0
    val openNotifications = { viewModel.navigate(Destination.Notifications) }
    val selectTab: (HomeTab) -> Unit = { viewModel.selectTab(it) }

    when (val screen = viewModel.current) {

        Destination.Home -> HomeScreen(
            state = viewModel.homeState,
            onNotifications = openNotifications,
            onInvitePlayer = { viewModel.navigate(Destination.InvitePlayer) },
            onStats = { viewModel.selectTab(HomeTab.Stats) },
            onMatchDetails = {
                viewModel.nextFixture?.let { viewModel.navigate(Destination.MatchDetails(it.id)) }
                    ?: viewModel.notify("No upcoming fixture")
            },
            onLineup = {
                viewModel.nextFixture?.let { viewModel.navigate(Destination.Lineup(it.id)) }
                    ?: viewModel.notify("No upcoming fixture")
            },
            onAttendance = {
                viewModel.nextFixture?.let { viewModel.navigate(Destination.Attendance(it.id)) }
                    ?: viewModel.notify("No upcoming fixture")
            },
            onViewResults = {
                viewModel.lastPlayedFixture?.let { viewModel.navigate(Destination.MatchReport(it.id)) }
                    ?: viewModel.selectTab(HomeTab.Home)
            },
            onSelectTab = selectTab
        )

        Destination.Stats -> StatsScreen(
            state = viewModel.statsState,
            onInputStats = {
                val fixture = viewModel.lastPlayedFixture ?: viewModel.nextFixture
                fixture?.let { viewModel.navigate(Destination.StatsInput(it.id)) }
                    ?: viewModel.notify("No fixtures to log")
            },
            onExportPdf = viewModel::exportStats,
            onViewFullMatchReport = {
                viewModel.lastPlayedFixture?.let { viewModel.navigate(Destination.MatchReport(it.id)) }
                    ?: viewModel.notify("No match played yet")
            },
            onNotifications = openNotifications,
            onSelectTab = selectTab
        )

        Destination.Profile -> ProfileScreen(
            state = viewModel.profileState,
            onNotifications = openNotifications,
            onEditProfile = { viewModel.navigate(Destination.EditProfile) },
            onEditTeamInfo = { viewModel.navigate(Destination.EditProfile) },
            onLanguageSelected = viewModel::setLanguage,
            onSignOut = viewModel::signOut,
            onSelectTab = selectTab
        )

        Destination.Notifications -> NotificationsScreen(
            state = viewModel.notificationsState,
            unreadIds = viewModel.unreadNotificationIds,
            onBack = { viewModel.back() },
            onMarkAllRead = viewModel::markAllNotificationsRead,
            onOpenNotification = viewModel::openNotification,
            onSelectTab = selectTab
        )

        Destination.InvitePlayer -> InvitePlayerScreen(
            state = InvitePlayerUiState(
                inviteCode = viewModel.team.clubCode,
                directLink = viewModel.team.inviteLink
            ),
            onCopyCode = {
                clipboard.setText(AnnotatedString(viewModel.team.clubCode))
                viewModel.notify("Invite code copied")
            },
            onShareLink = {
                val share = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Join ${viewModel.team.name} on FormUp: ${viewModel.team.inviteLink} " +
                                "(code ${viewModel.team.clubCode})"
                    )
                }
                context.startActivity(Intent.createChooser(share, "Share invite"))
            },
            onDownloadQr = { viewModel.notify("QR code saved to downloads") },
            onDone = { viewModel.back() },
            onNotifications = openNotifications,
            onSelectTab = selectTab
        )

        Destination.EditProfile -> EditProfileScreen(
            coachName = viewModel.coach.name,
            coachRole = viewModel.coach.role,
            teamName = viewModel.team.name,
            squad = viewModel.team.squad,
            season = viewModel.team.season,
            homeGround = viewModel.team.homeGround,
            hasUnread = hasUnread,
            onBack = { viewModel.back() },
            onNotifications = openNotifications,
            onSave = { name, role, team, squad, season, ground ->
                viewModel.updateProfile(name, role)
                viewModel.updateTeam(team, squad, season, ground)
                viewModel.saveProfileEdits()
            },
            onSelectTab = selectTab
        )

        is Destination.StatsInput -> {
            val inputState = viewModel.statsInputState(screen.fixtureId)

            StatsInputScreen(
                state = inputState,
                onBack = { viewModel.back() },
                onCustomizeColumns = {
                    viewModel.notify("Column customization coming soon")
                },
                onCancel = { viewModel.back() },
                onSave = { playerRows, homeScore, awayScore ->
                    viewModel.saveMatchStats(
                        screen.fixtureId,
                        playerRows,
                        homeScore,
                        awayScore
                    )
                },
                onNotifications = openNotifications,
                onSelectTab = selectTab
            )
        }

        is Destination.MatchReport -> MatchReportScreen(
            state = viewModel.matchReportState(screen.fixtureId),
            onBack = { viewModel.back() },
            onNotifications = openNotifications,
            onSelectTab = selectTab
        )

        Destination.Calendar, Destination.Team, is Destination.MatchDetails, is Destination.Attendance, is Destination.Lineup -> {
            Scaffold(
                containerColor = FormUpColors.Background,
                topBar = {
                    FormUpTopBar(hasUnread = hasUnread, onNotifications = openNotifications)
                },
                bottomBar = {
                    FormUpBottomBar(selected = viewModel.currentTab, onSelect = selectTab)
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val screenName = when (screen) {
                            Destination.Calendar -> "Calendar"
                            Destination.Team -> "Team"
                            is Destination.MatchDetails -> "Match Details"
                            is Destination.Attendance -> "Attendance"
                            is Destination.Lineup -> "Lineup"
                            else -> "Screen"
                        }
                        Text(
                            text = "$screenName Coming Soon",
                            style = MaterialTheme.typography.titleMedium,
                            color = FormUpColors.TextPrimary
                        )
                        if (viewModel.canGoBack) {
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.back() },
                                colors = ButtonDefaults.buttonColors(containerColor = FormUpColors.Primary)
                            ) {
                                Text("Go Back", color = FormUpColors.Surface)
                            }
                        }
                    }
                }
            }
        }
    }
}
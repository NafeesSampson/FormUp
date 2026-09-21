package com.formup.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.formup.app.ui.auth.AccountRole
import com.formup.app.ui.auth.CreateTeamScreen
import com.formup.app.ui.auth.LoginScreen
import com.formup.app.ui.auth.SignUpScreen
import com.formup.app.ui.calendar.AttendanceScreen
import com.formup.app.ui.calendar.CalendarScreen
import com.formup.app.ui.calendar.ManageEventScreen
import com.formup.app.ui.calendar.MatchDetailsScreen
import com.formup.app.ui.calendar.SampleCalendarState
import com.formup.app.ui.home.HomeScreen
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.invite.InvitePlayerScreen
import com.formup.app.ui.notifications.NotificationsScreen
import com.formup.app.ui.profile.ProfileScreen
import com.formup.app.ui.stats.MatchReportScreen
import com.formup.app.ui.stats.StatsInputScreen
import com.formup.app.ui.stats.StatsScreen
import com.formup.app.ui.team.AddPlayerScreen
import com.formup.app.ui.team.SampleTeamState
import com.formup.app.ui.team.TeamScreen
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.formup.app.ui.auth.emailSignIn
import com.formup.app.ui.auth.emailSignUp
import com.formup.app.ui.auth.googleSignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

private enum class AppScreen {
    Login,
    SignUp,
    CreateTeam,
    Home,
    Notifications,
    InvitePlayer,
    Team,
    AddPlayer,
    Calendar,
    ManageEvent,
    MatchDetails,
    Attendance,
    Stats,
    StatsInput,
    MatchReport,
    Profile
}

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
private fun FormUpApp() {
    var screen by remember {
        mutableStateOf(if (FirebaseAuth.getInstance().currentUser != null) AppScreen.Home else AppScreen.Login)
    }
    var teamState by remember { mutableStateOf(SampleTeamState) }
    var calendarEvents by remember { mutableStateOf(SampleCalendarState.events) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun attempt(block: suspend () -> Boolean) {
        scope.launch {
            try {
                screen = if (block()) AppScreen.Home else AppScreen.CreateTeam
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
    fun routeTab(tab: HomeTab): AppScreen = when (tab) {
        HomeTab.Home -> AppScreen.Home
        HomeTab.Calendar -> AppScreen.Calendar
        HomeTab.Team -> AppScreen.Team
        HomeTab.Stats -> AppScreen.Stats
        HomeTab.Profile -> AppScreen.Profile
    }

    when (screen) {
        AppScreen.Login -> LoginScreen(
            onSignIn = { email, password, _ -> attempt { emailSignIn(email, password) } },
            onGoogleSignIn = { attempt { googleSignIn(context) } },
            onForgotPassword = { /* not built yet */ },
            onNavigateToSignUp = { screen = AppScreen.SignUp }
        )

        AppScreen.SignUp -> SignUpScreen(
            onCreateAccount = { account -> attempt { emailSignUp(account.fullName, account.email, account.password) } },
            onGoogleSignUp = { attempt { googleSignIn(context) } },
            onNavigateToLogin = { screen = AppScreen.Login }
        )

        AppScreen.CreateTeam -> CreateTeamScreen(
            onBack = { screen = AppScreen.SignUp },
            onCancel = { screen = AppScreen.Home },
            onTeamCreated = { team ->
                // team.joinCode is ready here for whoever wires it into the
                // "invite players with this code" surface later.
                screen = AppScreen.Home
            }
        )

        AppScreen.Home -> HomeScreen(
            onNotifications = { screen = AppScreen.Notifications },
            onInvitePlayer = { screen = AppScreen.InvitePlayer },
            onSelectTab = { screen = routeTab(it) }
        )

        AppScreen.Notifications -> NotificationsScreen(
            onBack = { screen = AppScreen.Home }
        )

        AppScreen.InvitePlayer -> InvitePlayerScreen(
            onNotifications = { screen = AppScreen.Notifications },
            onDone = { screen = AppScreen.Home }
        )

        AppScreen.Team -> TeamScreen(
            state = teamState,
            onNotifications = { screen = AppScreen.Notifications },
            onAddPlayer = { screen = AppScreen.AddPlayer },
            onSelectTab = { screen = routeTab(it) }
        )

        AppScreen.AddPlayer -> AddPlayerScreen(
            onBack = { screen = AppScreen.Team },
            onSave = { newPlayer ->
                teamState = teamState.copy(
                    players = teamState.players + newPlayer
                )
                screen = AppScreen.Team
            }
        )

        AppScreen.Calendar -> CalendarScreen(
            state = SampleCalendarState.copy(events = calendarEvents),
            onNotifications = { screen = AppScreen.Notifications },
            onManageTrainingOrLineup = { screen = AppScreen.ManageEvent },
            onViewMatchDetails = { screen = AppScreen.MatchDetails },
            onViewTeamAttendance = { screen = AppScreen.Attendance },
            onSelectTab = { screen = routeTab(it) }
        )

        AppScreen.ManageEvent -> ManageEventScreen(
            onBack = { screen = AppScreen.Calendar },
            onEventCreated = { event ->
                calendarEvents = calendarEvents + event
                screen = AppScreen.Calendar
            }
        )

        AppScreen.MatchDetails -> MatchDetailsScreen(
            onBack = { screen = AppScreen.Calendar }
        )

        AppScreen.Attendance -> AttendanceScreen(
            onBack = { screen = AppScreen.Calendar }
        )

        AppScreen.Stats -> StatsScreen(
            onNotifications = { screen = AppScreen.Notifications },
            onInputStats = { screen = AppScreen.StatsInput },
            onViewFullMatchReport = { screen = AppScreen.MatchReport },
            onSelectTab = { screen = routeTab(it) }
        )

        AppScreen.StatsInput -> StatsInputScreen(
            onBack = { screen = AppScreen.Stats },
            onCancel = { screen = AppScreen.Stats },
            onSave = { screen = AppScreen.Stats },
            onNotifications = { screen = AppScreen.Notifications }
        )

        AppScreen.MatchReport -> MatchReportScreen(
            onBack = { screen = AppScreen.Stats },
            onNotifications = { screen = AppScreen.Notifications }
        )

        AppScreen.Profile -> ProfileScreen(
            onNotifications = { screen = AppScreen.Notifications },
            onSelectTab = { screen = routeTab(it) }
        )
    }
}
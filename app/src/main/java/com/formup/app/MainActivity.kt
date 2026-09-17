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
import com.formup.app.ui.home.HomeScreen
import com.formup.app.ui.invite.InvitePlayerScreen
import com.formup.app.ui.notifications.NotificationsScreen
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme

private enum class AppScreen { Home, Notifications, InvitePlayer }

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
    var screen by remember { mutableStateOf(AppScreen.Home) }

    when (screen) {
        AppScreen.Home -> HomeScreen(
            onNotifications = { screen = AppScreen.Notifications },
            onInvitePlayer = { screen = AppScreen.InvitePlayer }
        )
        AppScreen.Notifications -> NotificationsScreen(
            onBack = { screen = AppScreen.Home }
        )
        AppScreen.InvitePlayer -> InvitePlayerScreen(
            onNotifications = { screen = AppScreen.Notifications },
            onDone = { screen = AppScreen.Home }
        )
    }
}

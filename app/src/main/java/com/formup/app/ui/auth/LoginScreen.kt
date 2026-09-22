package com.formup.app.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.auth.components.AuthTextField
import com.formup.app.ui.auth.components.GoogleAuthButton
import com.formup.app.ui.auth.components.OrDivider
import com.formup.app.ui.auth.components.RoleToggle
import com.formup.app.ui.home.components.IconBubble
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme

// Holds error messages for the login form inputs
data class LoginFormErrors(
    val email: String? = null,
    val password: String? = null
)

// Main login screen ui component
@Composable
fun LoginScreen(
    // Action triggered when user submits valid credentials
    onSignIn: (email: String, password: String, role: AccountRole) -> Unit = { _, _, _ -> },
    // Navigation actions for screen transitions
    onForgotPassword: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Stores the current user input values and error state in screen memory
    var role by remember { mutableStateOf(AccountRole.COACH) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf(LoginFormErrors()) }

    // Validates inputs and submits the form
    fun submit() {
        val emailError = if (email.isBlank()) "Enter your email address" else null
        val passwordError = if (password.isBlank()) "Enter your password" else null

        // Stop submission if any input field is empty
        if (emailError != null || passwordError != null) {
            errors = LoginFormErrors(emailError, passwordError)
            return
        }

        // Clear error messages and trigger sign-in
        errors = LoginFormErrors()
        onSignIn(email.trim(), password, role)
    }

    // Top-level layout container that sets screen background color
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background
    ) { inner ->
        // Vertically centers the login card on the screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Rounded card container holding all input elements
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = FormUpColors.Surface,
                border = BorderStroke(1.dp, FormUpColors.Hairline)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App brand icon bubble ("F")
                    IconBubble(size = 64.dp, background = FormUpColors.Primary) {
                        Text("F", color = FormUpColors.Surface, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                    }

                    Spacer(Modifier.height(16.dp))

                    // Header text titles
                    Text("Welcome Back", style = MaterialTheme.typography.headlineMedium, color = FormUpColors.Primary)
                    Spacer(Modifier.height(4.dp))
                    Text("Sign in to your account", style = MaterialTheme.typography.bodyMedium, color = FormUpColors.TextSecondary)

                    Spacer(Modifier.height(20.dp))

                    // Email input field
                    Column(Modifier.fillMaxWidth()) {
                        Text("Email Address", style = MaterialTheme.typography.bodySmall, color = FormUpColors.TextPrimary, modifier = Modifier.padding(bottom = 6.dp))
                        AuthTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "coach@example.com",
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = FormUpColors.TextSecondary) },
                            keyboardType = KeyboardType.Email,
                            error = errors.email
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Password input field
                    Column(Modifier.fillMaxWidth()) {
                        Text("Password", style = MaterialTheme.typography.bodySmall, color = FormUpColors.TextPrimary, modifier = Modifier.padding(bottom = 6.dp))
                        AuthTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "••••••••",
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = FormUpColors.TextSecondary) },
                            isPassword = true,
                            error = errors.password
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Clickable "Forgot Password?" text aligned to the right
                    Text(
                        "Forgot Password?",
                        style = MaterialTheme.typography.bodySmall,
                        color = FormUpColors.Primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onForgotPassword),
                        textAlign = TextAlign.End
                    )

                    Spacer(Modifier.height(16.dp))

                    // Main "Sign In" button
                    Button(
                        onClick = ::submit,
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FormUpColors.Primary, contentColor = FormUpColors.Surface),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("Sign In", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(Modifier.height(18.dp))


                    OrDivider("or continue with")

                    Spacer(Modifier.height(14.dp))

                    // google authentication button
                    GoogleAuthButton(text = "Google", onClick = onGoogleSignIn)

                    Spacer(Modifier.height(18.dp))

                    // Bottom text link navigating to the Sign Up screen
                    Row {
                        Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium, color = FormUpColors.TextSecondary)
                        Text(
                            "Sign Up",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FormUpColors.Primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = onNavigateToSignUp)
                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, widthDp = 390, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    FormUpTheme { LoginScreen() }
}
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

data class LoginFormErrors(
    val email: String? = null,
    val password: String? = null
)

@Composable
fun LoginScreen(
    onSignIn: (email: String, password: String, role: AccountRole) -> Unit = { _, _, _ -> },
    onForgotPassword: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var role by remember { mutableStateOf(AccountRole.COACH) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf(LoginFormErrors()) }

    fun submit() {
        val emailError = if (email.isBlank()) "Enter your email address" else null
        val passwordError = if (password.isBlank()) "Enter your password" else null
        if (emailError != null || passwordError != null) {
            errors = LoginFormErrors(emailError, passwordError)
            return
        }
        errors = LoginFormErrors()
        onSignIn(email.trim(), password, role)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
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
                    IconBubble(size = 64.dp, background = FormUpColors.Primary) {
                        Text("F", color = FormUpColors.Surface, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Welcome Back", style = MaterialTheme.typography.headlineMedium, color = FormUpColors.Primary)
                    Spacer(Modifier.height(4.dp))
                    Text("Sign in to your account", style = MaterialTheme.typography.bodyMedium, color = FormUpColors.TextSecondary)

                    Spacer(Modifier.height(20.dp))
                    RoleToggle(selected = role, onSelect = { role = it })

                    Spacer(Modifier.height(18.dp))
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
                    GoogleAuthButton(text = "Google", onClick = onGoogleSignIn)

                    Spacer(Modifier.height(18.dp))
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
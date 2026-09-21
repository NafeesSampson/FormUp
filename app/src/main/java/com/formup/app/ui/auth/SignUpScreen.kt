package com.formup.app.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.auth.components.AuthFieldLabel
import com.formup.app.ui.auth.components.AuthTextField
import com.formup.app.ui.auth.components.GoogleAuthButton
import com.formup.app.ui.auth.components.OrDivider
import com.formup.app.ui.auth.components.RoleToggle
import com.formup.app.ui.home.components.IconBubble
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.FormUpTheme

data class SignUpFormErrors(
    val fullName: String? = null,
    val email: String? = null,
    val password: String? = null
)

data class NewAccount(
    val fullName: String,
    val email: String,
    val password: String,
    val role: AccountRole
)

@Composable
fun SignUpScreen(
    onCreateAccount: (NewAccount) -> Unit = {},
    onGoogleSignUp: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Defaulted to Coach: this build only wires the coach flow through to Create Team.
    var role by remember { mutableStateOf(AccountRole.COACH) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf(SignUpFormErrors()) }

    fun submit() {
        val nameError = if (fullName.isBlank()) "Enter your full name" else null
        val emailError = if (email.isBlank()) "Enter your email address" else null
        val passwordError = when {
            password.isBlank() -> "Enter a password"
            password.length < 8 -> "Must be at least 8 characters long"
            else -> null
        }
        if (nameError != null || emailError != null || passwordError != null) {
            errors = SignUpFormErrors(nameError, emailError, passwordError)
            return
        }
        errors = SignUpFormErrors()
        onCreateAccount(NewAccount(fullName.trim(), email.trim(), password, role))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FormUpColors.Background
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = FormUpColors.Surface,
                border = BorderStroke(1.dp, FormUpColors.Hairline)
            ) {
                Column {
                    // Accent bar matching the mockup's top edge stripe.
                    androidx.compose.foundation.layout.Box(
                        Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(FormUpColors.PrimaryDeep)
                    )

                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconBubble(size = 64.dp, background = FormUpColors.Primary) {
                            Text("F", color = FormUpColors.Surface, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("FormUp", style = MaterialTheme.typography.headlineMedium, color = FormUpColors.Primary)
                        Spacer(Modifier.height(4.dp))
                        Text("Elevate your game. Sign up to get started.", style = MaterialTheme.typography.bodyMedium, color = FormUpColors.TextSecondary)

                        Spacer(Modifier.height(22.dp))




                        Column(Modifier.fillMaxWidth()) {
                            AuthFieldLabel("Full Name")
                            AuthTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                placeholder = "Jane Doe",
                                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = FormUpColors.TextSecondary) },
                                error = errors.fullName
                            )
                        }

                        Spacer(Modifier.height(14.dp))
                        Column(Modifier.fillMaxWidth()) {
                            AuthFieldLabel("Email Address")
                            AuthTextField(
                                value = email,
                                onValueChange = { email = it },
                                placeholder = "jane.doe@example.com",
                                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = FormUpColors.TextSecondary) },
                                keyboardType = KeyboardType.Email,
                                error = errors.email
                            )
                        }

                        Spacer(Modifier.height(14.dp))
                        Column(Modifier.fillMaxWidth()) {
                            AuthFieldLabel("Password")
                            AuthTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = "••••••••",
                                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = FormUpColors.TextSecondary) },
                                isPassword = true,
                                error = errors.password,
                                helperText = if (errors.password == null) "Must be at least 8 characters long." else null
                            )
                        }

                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = ::submit,
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FormUpColors.Primary, contentColor = FormUpColors.Surface),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Text("Create Account", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.size(8.dp))
                            Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }

                        Spacer(Modifier.height(18.dp))
                        OrDivider("OR")
                        Spacer(Modifier.height(14.dp))
                        GoogleAuthButton(text = "Sign up with Google", onClick = onGoogleSignUp)

                        Spacer(Modifier.height(18.dp))
                        Row {
                            Text("Already have an account? ", style = MaterialTheme.typography.bodyMedium, color = FormUpColors.TextSecondary)
                            Text(
                                "Log In",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FormUpColors.Primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable(onClick = onNavigateToLogin)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 950)
@Composable
private fun SignUpScreenPreview() {
    FormUpTheme { SignUpScreen() }
}
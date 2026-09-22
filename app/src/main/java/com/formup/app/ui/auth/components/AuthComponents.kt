package com.formup.app.ui.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.formup.app.ui.auth.AccountRole
import com.formup.app.ui.theme.FormUpColors
import com.formup.app.ui.theme.Mono


//these are just jetcompose components
@Composable
fun RoleToggle(
    selected: AccountRole,
    onSelect: (AccountRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(FormUpColors.NavIndicator, RoundedCornerShape(10.dp))
            .padding(3.dp)
    ) {
        RoleOption("Coach", AccountRole.COACH, selected, onSelect, Modifier.weight(1f))
        RoleOption("Player", AccountRole.PLAYER, selected, onSelect, Modifier.weight(1f))
    }
}

@Composable
private fun RoleOption(
    label: String,
    value: AccountRole,
    selected: AccountRole,
    onSelect: (AccountRole) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = value == selected
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(value) },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) FormUpColors.Surface else androidx.compose.ui.graphics.Color.Transparent
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 9.dp)) {
            Text(
                label,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (isSelected) FormUpColors.Primary else FormUpColors.TextSecondary
            )
        }
    }
}


@Composable
fun AuthFieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontFamily = Mono,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
        color = FormUpColors.TextSecondary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null,
    helperText: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = FormUpColors.TextSecondary) },
            leadingIcon = leadingIcon,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = FormUpColors.TextSecondary
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FormUpColors.Surface,
                unfocusedContainerColor = FormUpColors.Surface,
                focusedBorderColor = FormUpColors.Primary,
                unfocusedBorderColor = FormUpColors.Hairline,
                cursorColor = FormUpColors.Primary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(error, fontFamily = Mono, fontSize = 11.sp, color = FormUpColors.Danger, modifier = Modifier.padding(top = 4.dp, start = 2.dp))
        } else if (helperText != null) {
            Text(helperText, style = MaterialTheme.typography.bodySmall, color = FormUpColors.TextSecondary, modifier = Modifier.padding(top = 4.dp, start = 2.dp))
        }
    }
}


@Composable
fun OrDivider(label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = FormUpColors.Hairline)
        Text(
            label,
            fontFamily = Mono,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp,
            color = FormUpColors.TextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = FormUpColors.Hairline)
    }
}


@Composable
fun GoogleAuthButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = FormUpColors.Surface,
        border = BorderStroke(1.dp, FormUpColors.Hairline)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("G", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FormUpColors.TextPrimary)
            Box(Modifier.padding(horizontal = 4.dp))
            Text(text, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = FormUpColors.TextPrimary)
        }
    }
}
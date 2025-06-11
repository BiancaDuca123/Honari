package com.honari.app.presentation.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.honari.app.R
import com.honari.app.presentation.theme.HonariTheme

@Composable
fun PasswordScreen(navController: NavController, viewModel: AuthViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    val onSendReset = {
        focusManager.clearFocus()
        if (email.isNotBlank()) viewModel.sendPasswordResetEmail(email)
    }
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        PasswordFormColumn(
            email = email,
            onEmailChange = { email = it },
            isLoading = uiState.isLoading,
            error = uiState.error,
            resetSent = uiState.passwordResetSent,
            onSendReset = onSendReset,
            onBack = { navController.popBackStack() }
        )
    }
}

@Composable
private fun PasswordFormColumn(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    error: String?,
    resetSent: Boolean,
    onSendReset: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        PasswordHeader()
        Spacer(modifier = Modifier.height(24.dp))
        PasswordEmailField(email = email, onEmailChange = onEmailChange, onSubmit = onSendReset)
        Spacer(modifier = Modifier.height(24.dp))
        PasswordResetButton(
            isLoading = isLoading,
            enabled = email.isNotBlank() && !isLoading,
            onClick = onSendReset
        )
        Spacer(modifier = Modifier.height(16.dp))
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (resetSent) {
            Text(
                text = stringResource(R.string.password_reset_email_sent),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = onBack) {
            Text(
                text = stringResource(R.string.back_to_login),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PasswordHeader() {
    Icon(
        imageVector = Icons.Default.AutoStories,
        contentDescription = null,
        modifier = Modifier.size(80.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = stringResource(R.string.password_reset_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    Text(
        text = stringResource(R.string.password_reset_instructions),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 32.dp)
    )
}

@Composable
private fun PasswordEmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    OutlinedTextField(
        value = email, onValueChange = onEmailChange,
        label = { Text(stringResource(R.string.email)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun PasswordResetButton(isLoading: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.send_reset_link),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(name = "Password Reset – Light", showBackground = true, showSystemUi = true)
@Composable
private fun PasswordScreenLightPreview() {
    HonariTheme(darkTheme = false) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            PasswordFormColumn(
                email = "bianca@honari.app",
                onEmailChange = {},
                isLoading = false,
                error = null,
                resetSent = false,
                onSendReset = {},
                onBack = {}
            )
        }
    }
}

@Preview(
    name = "Password Reset – Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PasswordScreenDarkPreview() {
    HonariTheme(darkTheme = true) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            PasswordFormColumn(
                email = "bianca@honari.app",
                onEmailChange = {},
                isLoading = false,
                error = null,
                resetSent = true,
                onSendReset = {},
                onBack = {}
            )
        }
    }
}

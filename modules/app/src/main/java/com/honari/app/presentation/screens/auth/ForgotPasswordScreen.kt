package com.honari.app.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.honari.app.presentation.theme.AccentAmber
import com.honari.app.presentation.theme.AccentCoral
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal

private val ForgotGradient = Brush.verticalGradient(listOf(AccentCoral, AccentAmber))
private const val HERO_HEIGHT = 260

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var email by rememberSaveable { mutableStateOf("") }
    var sent by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) { sent = true; viewModel.clearSuccess() }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ForgotHero(onBack = onNavigateBack)
            if (sent) {
                ResetSentContent(onBackToLogin = onNavigateBack)
            } else {
                ForgotForm(
                    email = email,
                    onEmailChange = { email = it },
                    isLoading = uiState.isLoading,
                    onSend = { viewModel.sendPasswordReset(email) },
                    onBack = onNavigateBack,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ) { data ->
            Snackbar(containerColor = ErrorRed, contentColor = CardWhite) {
                Text(text = data.visuals.message)
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = PrimaryTeal) }
        }
    }
}

@Composable
private fun ForgotHero(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(HERO_HEIGHT.dp).background(ForgotGradient),
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(8.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = CardWhite,
            )
        }
        Icon(
            imageVector = Icons.Default.LockReset,
            contentDescription = null,
            tint = CardWhite.copy(alpha = 0.07f),
            modifier = Modifier.size(200.dp).align(Alignment.Center),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 28.dp),
        ) {
            Icon(
                imageVector = Icons.Default.LockReset,
                contentDescription = null,
                tint = CardWhite,
                modifier = Modifier.size(56.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Reset Password",
                style = MaterialTheme.typography.headlineMedium,
                color = CardWhite,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                "We'll send you a reset link",
                style = MaterialTheme.typography.bodyMedium,
                color = CardWhite.copy(alpha = 0.85f),
            )
        }
    }
}

@Composable
private fun ForgotForm(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    onSend: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Enter the email address associated with your Honari account and we'll send you a link to reset your password.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email address",
            leadingIcon = { Icon(Icons.Default.Email, null, tint = AccentCoral) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSend,
            enabled = !isLoading && email.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentCoral,
                contentColor = CardWhite,
            ),
        ) {
            Text(
                "Send Reset Link",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ResetSentContent(onBackToLogin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = PrimaryTeal,
            modifier = Modifier.size(80.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Email Sent!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryTeal,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Check your inbox for a password reset link. Don't forget to check your spam folder.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryTeal,
                contentColor = CardWhite,
            ),
        ) {
            Text("Back to Sign In", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

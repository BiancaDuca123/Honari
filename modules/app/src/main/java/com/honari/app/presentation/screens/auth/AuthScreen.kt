package com.honari.app.presentation.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.honari.app.R
import com.honari.app.presentation.theme.BackgroundBeige
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.BrownLight
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.TextPrimary
import com.honari.app.presentation.theme.TextSecondary

@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val typography = MaterialTheme.typography

    val googleSignInClient = remember {
        getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build(),
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            runCatching {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let(viewModel::signInWithGoogle) ?: viewModel.clearError()
            }.onFailure {
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            HeroContent(typography = typography)
            Spacer(modifier = Modifier.height(48.dp))
            ContinueDivider(typography = typography)
            Spacer(modifier = Modifier.height(16.dp))
            GoogleSignInButton(
                onClick = { launcher.launch(googleSignInClient.signInIntent) },
                enabled = !uiState.isLoading,
                typography = typography,
            )
            Spacer(modifier = Modifier.height(20.dp))
            AuthDescription(typography = typography)
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        ) { data ->
            Snackbar(containerColor = ErrorRed, contentColor = CardWhite) {
                Text(text = data.visuals.message, style = typography.bodyMedium)
            }
        }

        if (uiState.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
private fun HeroContent(typography: androidx.compose.material3.Typography) {
    Icon(
        imageVector = Icons.Default.MenuBook,
        contentDescription = null,
        tint = BrownHeadline,
        modifier = Modifier.size(80.dp),
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Honari",
        style = typography.displayMedium,
        color = BrownHeadline,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Your personal book companion",
        style = typography.bodyLarge,
        color = TextSecondary,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ContinueDivider(typography: androidx.compose.material3.Typography) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BrownLight.copy(alpha = 0.3f),
        )
        Text(
            text = "  Continue With  ",
            color = BrownLight,
            style = typography.labelLarge,
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BrownLight.copy(alpha = 0.3f),
        )
    }
}

@Composable
private fun GoogleSignInButton(
    onClick: () -> Unit,
    enabled: Boolean,
    typography: androidx.compose.material3.Typography,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BrownLight.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardWhite,
            contentColor = TextPrimary,
            disabledContainerColor = CardWhite,
            disabledContentColor = TextPrimary.copy(alpha = 0.5f),
        ),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "Login with Google",
            color = TextPrimary,
            style = typography.titleSmall,
        )
    }
}

@Composable
private fun AuthDescription(typography: androidx.compose.material3.Typography) {
    Text(
        text = "Sign in to discover new books, scan your latest finds, and build " +
            "a library that feels like home.",
        style = typography.bodyMedium,
        color = TextSecondary,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = PrimaryTeal)
    }
}

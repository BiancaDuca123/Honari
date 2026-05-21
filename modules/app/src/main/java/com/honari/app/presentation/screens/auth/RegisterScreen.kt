package com.honari.app.presentation.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.honari.app.R
import com.honari.app.presentation.theme.AccentCoral
import com.honari.app.presentation.theme.AccentPurple
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.PrimaryTealDark

private val RegisterGradient = Brush.verticalGradient(listOf(AccentPurple, PrimaryTeal))
private const val HERO_HEIGHT = 240
private const val MIN_PASSWORD_LENGTH = 6

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current

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
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            runCatching {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
                account.idToken?.let(viewModel::signInWithGoogle) ?: viewModel.clearError()
            }.onFailure { viewModel.clearError() }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            RegisterHero(onBack = onNavigateToLogin)
            RegisterForm(
                isLoading = uiState.isLoading,
                onRegister = { name, email, pass -> viewModel.registerWithEmail(email, name, pass) },
                onGoogleRegister = { launcher.launch(googleSignInClient.signInIntent) },
                onLoginClick = onNavigateToLogin,
            )
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
private fun RegisterHero(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(HERO_HEIGHT.dp).background(RegisterGradient),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.AutoStories,
            contentDescription = null,
            tint = CardWhite.copy(alpha = 0.07f),
            modifier = Modifier.size(200.dp),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.statusBarsPadding(),
        ) {
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                tint = CardWhite,
                modifier = Modifier.size(56.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                color = CardWhite,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                "Join the Honari reading community",
                style = MaterialTheme.typography.bodyMedium,
                color = CardWhite.copy(alpha = 0.85f),
            )
        }
    }
}

@Composable
private fun RegisterForm(
    isLoading: Boolean,
    onRegister: (name: String, email: String, password: String) -> Unit,
    onGoogleRegister: () -> Unit,
    onLoginClick: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val isValid = name.isNotBlank() && email.isNotBlank() &&
        password.length >= MIN_PASSWORD_LENGTH && password == confirmPassword

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        AuthTextField(
            value = name,
            onValueChange = { name = it },
            label = "Display Name",
            leadingIcon = { Icon(Icons.Default.Person, null, tint = AccentPurple) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        Spacer(modifier = Modifier.height(12.dp))
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            leadingIcon = { Icon(Icons.Default.Email, null, tint = AccentPurple) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        AuthTextField(
            value = password,
            onValueChange = { password = it; passwordError = null },
            label = "Password (min 6 chars)",
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = AccentPurple) },
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = AccentPurple,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        AuthTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                passwordError = if (it != password) "Passwords don't match" else null
            },
            label = "Confirm Password",
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = AccentPurple) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
        )
        passwordError?.let { err ->
            Text(
                text = err,
                color = ErrorRed,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp),
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { onRegister(name, email, password) },
            enabled = !isLoading && isValid,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPurple,
                contentColor = CardWhite,
            ),
        ) {
            Text("Create Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            Text(" or ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        }
        Spacer(modifier = Modifier.height(20.dp))
        GoogleSignInButton(onClick = onGoogleRegister, enabled = !isLoading)
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Already have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                " Sign In",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = AccentCoral,
                modifier = Modifier.clickable(onClick = onLoginClick),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

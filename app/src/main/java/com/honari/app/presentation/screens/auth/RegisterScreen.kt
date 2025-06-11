package com.honari.app.presentation.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.honari.app.R
import com.honari.app.presentation.navigation.Screen
import com.honari.app.presentation.theme.HonariTheme

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val canSubmit = displayName.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
        password == confirmPassword && !uiState.isLoading
    val onSubmit = { if (canSubmit) viewModel.register(email, password, displayName) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RegisterFormColumn(
            displayName = displayName,
            onDisplayNameChange = { displayName = it },
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            confirmPassword = confirmPassword,
            onConfirmChange = { confirmPassword = it },
            passwordVisible = passwordVisible,
            onTogglePassword = { passwordVisible = !passwordVisible },
            confirmVisible = confirmPasswordVisible,
            onToggleConfirm = { confirmPasswordVisible = !confirmPasswordVisible },
            canSubmit = canSubmit,
            isLoading = uiState.isLoading,
            onSubmit = onSubmit,
            onSignIn = { navController.navigateUp() },
            onFocusMoveDown = { focusManager.moveFocus(FocusDirection.Down) },
            onFocusClear = { focusManager.clearFocus() },
        )
        RegisterErrorSnackbar(error = uiState.error, onDismiss = viewModel::clearError)
    }
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            navController.navigate(Screen.MainNav.route) { popUpTo(0) }
        }
    }
}

@Composable
private fun RegisterFormColumn(
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePassword: () -> Unit,
    confirmVisible: Boolean,
    onToggleConfirm: () -> Unit,
    canSubmit: Boolean,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onSignIn: () -> Unit,
    onFocusMoveDown: () -> Unit,
    onFocusClear: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        RegisterHeader()
        Spacer(modifier = Modifier.height(40.dp))
        RegisterNameField(value = displayName, onChange = onDisplayNameChange, onNext = onFocusMoveDown)
        Spacer(modifier = Modifier.height(16.dp))
        RegisterEmailField(value = email, onChange = onEmailChange, onNext = onFocusMoveDown)
        Spacer(modifier = Modifier.height(16.dp))
        RegisterPasswordField(value = password, visible = passwordVisible, onChange = onPasswordChange,
            onToggle = onTogglePassword, onNext = onFocusMoveDown)
        Spacer(modifier = Modifier.height(16.dp))
        RegisterConfirmField(
            value = confirmPassword, visible = confirmVisible,
            isError = confirmPassword.isNotEmpty() && password != confirmPassword,
            onChange = onConfirmChange, onToggle = onToggleConfirm,
            onDone = { onFocusClear(); onSubmit() })
        Spacer(modifier = Modifier.height(32.dp))
        RegisterButton(enabled = canSubmit, isLoading = isLoading, onClick = onSubmit)
        Spacer(modifier = Modifier.height(24.dp))
        RegisterFooter(onSignIn = onSignIn)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RegisterHeader() {
    Icon(
        imageVector = Icons.Default.AutoStories,
        contentDescription = null,
        modifier = Modifier.size(80.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = stringResource(R.string.create_account),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = stringResource(R.string.register_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun RegisterNameField(value: String, onChange: (String) -> Unit, onNext: () -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(stringResource(R.string.display_name)) },
        leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        singleLine = true, shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
    )
}

@Composable
private fun RegisterEmailField(value: String, onChange: (String) -> Unit, onNext: () -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(stringResource(R.string.email)) },
        leadingIcon = { Icon(Icons.Default.Email, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        singleLine = true, shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
    )
}

@Composable
private fun RegisterPasswordField(
    value: String,
    visible: Boolean,
    onChange: (String) -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit
) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(stringResource(R.string.password)) },
        leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle", tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        singleLine = true, shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
    )
}

@Composable
private fun RegisterConfirmField(
    value: String,
    visible: Boolean,
    isError: Boolean,
    onChange: (String) -> Unit,
    onToggle: () -> Unit,
    onDone: () -> Unit
) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(stringResource(R.string.confirm_password)) },
        leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle", tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        singleLine = true, shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            errorBorderColor = MaterialTheme.colorScheme.error
        ),
        isError = isError,
    )
    if (isError) {
        Text(
            text = stringResource(R.string.passwords_do_not_match),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
        )
    }
}

@Composable
private fun RegisterButton(enabled: Boolean, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Text(text = stringResource(R.string.sign_up), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RegisterFooter(onSignIn: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(R.string.have_account), style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = stringResource(R.string.sign_in), style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onSignIn() })
    }
}

@Composable
private fun RegisterErrorSnackbar(error: String?, onDismiss: () -> Unit) {
    error?.let {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.dismiss)) } }
            ) { Text(it) }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(name = "Register – Light", showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenLightPreview() {
    HonariTheme(darkTheme = false) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            RegisterFormColumn(
                displayName = "Bianca", onDisplayNameChange = {},
                email = "bianca@honari.app", onEmailChange = {},
                password = "password", onPasswordChange = {},
                confirmPassword = "password", onConfirmChange = {},
                passwordVisible = false, onTogglePassword = {},
                confirmVisible = false, onToggleConfirm = {},
                canSubmit = true, isLoading = false, onSubmit = {},
                onSignIn = {}, onFocusMoveDown = {}, onFocusClear = {},
            )
        }
    }
}

@Preview(name = "Register – Dark", showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RegisterScreenDarkPreview() {
    HonariTheme(darkTheme = true) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            RegisterFormColumn(
                displayName = "Bianca", onDisplayNameChange = {},
                email = "bianca@honari.app", onEmailChange = {},
                password = "password", onPasswordChange = {},
                confirmPassword = "password", onConfirmChange = {},
                passwordVisible = false, onTogglePassword = {},
                confirmVisible = false, onToggleConfirm = {},
                canSubmit = true, isLoading = false, onSubmit = {},
                onSignIn = {}, onFocusMoveDown = {}, onFocusClear = {},
            )
        }
    }
}

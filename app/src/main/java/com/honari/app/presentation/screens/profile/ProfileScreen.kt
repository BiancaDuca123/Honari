package com.honari.app.presentation.screens.profile

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.honari.app.R
import com.honari.app.presentation.theme.HonariTheme
import com.honari.app.presentation.theme.RatingStarColor
import com.honari.app.presentation.theme.SuccessColor
import com.honari.app.presentation.theme.ThemePreference
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel,
    themePreference: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 20.dp, bottom = 28.dp),
        )
        ProfileAvatar(
            displayName = state.user?.displayName,
            email = state.user?.email,
            photoUrl = state.user?.profileImageUrl,
            memberSince = state.user?.createdAt,
        )
        Spacer(Modifier.height(24.dp))

        // Reading Goal card
        ReadingGoalCard(
            booksRead = state.booksRead,
            goal = state.readingGoal,
            onUpdateGoal = viewModel::updateReadingGoal,
        )
        Spacer(Modifier.height(16.dp))

        ReadingProgressCard(
            reading = state.currentlyReading,
            finished = state.booksRead,
            total = state.totalBooks
        )
        Spacer(Modifier.height(16.dp))
        StatsCard(
            reading = state.currentlyReading,
            finished = state.booksRead,
            wantToRead = state.wantToRead,
            total = state.totalBooks
        )
        Spacer(Modifier.height(16.dp))

        // Theme toggle
        ThemeToggleCard(themePreference = themePreference, onThemeChange = onThemeChange)
        Spacer(Modifier.height(24.dp))

        SignOutButton(onSignOut = { viewModel.signOut(onSignOut) })
        Spacer(Modifier.height(32.dp))
    }
}

// ── Reading Goal ──────────────────────────────────────────────────────────────

@Composable
private fun ReadingGoalCard(booksRead: Int, goal: Int, onUpdateGoal: (Int) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val progress = if (goal > 0) (booksRead.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val achieved = goal in 1..booksRead

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Circular progress arc
            Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round,
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = if (achieved) SuccessColor else MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (goal > 0) "$booksRead" else "—",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 16.sp,
                    )
                    if (goal > 0) Text(
                        text = "/$goal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reading Goal ${if (goal > 0) "${(progress * 100).toInt()}%" else ""}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = when {
                        goal == 0 -> "Tap to set your yearly book goal"
                        achieved -> "🎉 Goal achieved! Amazing work!"
                        booksRead == 0 -> "$goal books remaining this year"
                        else -> "${goal - booksRead} more to reach your goal"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (achieved) SuccessColor else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton24(icon = Icons.Default.Edit, tint = MaterialTheme.colorScheme.primary) {
                showDialog = true
            }
        }
    }

    if (showDialog) {
        GoalPickerDialog(
            currentGoal = goal,
            onConfirm = { newGoal -> onUpdateGoal(newGoal); showDialog = false },
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
private fun GoalPickerDialog(currentGoal: Int, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var input by remember { mutableStateOf(if (currentGoal > 0) currentGoal.toString() else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yearly Reading Goal", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "How many books do you want to read this year?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { if (it.length <= 3 && it.all(Char::isDigit)) input = it },
                    label = { Text("Books") },
                    placeholder = { Text("e.g. 24") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(input.toIntOrNull() ?: 0) }) {
                Text(
                    "Set Goal",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

// ── Theme Toggle ──────────────────────────────────────────────────────────────

@Composable
private fun ThemeToggleCard(
    themePreference: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Appearance",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeOption(
                    icon = Icons.Default.PhoneAndroid, label = "System",
                    selected = themePreference == ThemePreference.SYSTEM,
                    modifier = Modifier.weight(1f),
                    onClick = { onThemeChange(ThemePreference.SYSTEM) },
                )
                ThemeOption(
                    icon = Icons.Default.WbSunny, label = "Light",
                    selected = themePreference == ThemePreference.LIGHT,
                    modifier = Modifier.weight(1f),
                    onClick = { onThemeChange(ThemePreference.LIGHT) },
                )
                ThemeOption(
                    icon = Icons.Default.NightlightRound, label = "Dark",
                    selected = themePreference == ThemePreference.DARK,
                    modifier = Modifier.weight(1f),
                    onClick = { onThemeChange(ThemePreference.DARK) },
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val bg =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg =
        if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = bg, shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp),
        ) {
            Icon(icon, contentDescription = label, tint = fg, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = fg,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

// ── Existing cards ────────────────────────────────────────────────────────────

@Composable
private fun ProfileAvatar(
    displayName: String?,
    email: String?,
    photoUrl: String?,
    memberSince: Long?
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    displayName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            displayName ?: "—",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(2.dp))
        Text(
            email ?: "—",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (memberSince != null) {
            Spacer(Modifier.height(4.dp))
            val formatted =
                SimpleDateFormat("MMMM yyyy", LocalLocale.current.platformLocale).format(
                    Date(memberSince)
                )
            Text(
                stringResource(R.string.member_since, formatted),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ReadingProgressCard(reading: Int, finished: Int, total: Int) {
    val progress = if (total > 0) finished.toFloat() / total.toFloat() else 0f
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AutoStories,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.reading_progress),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.weight(1f))
                Text(
                    stringResource(R.string.books_progress, finished, total),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            if (reading > 0) {
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(
                        if (reading > 1) R.string.currently_reading_count_plural else R.string.currently_reading_count,
                        reading
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StatsCard(reading: Int, finished: Int, wantToRead: Int, total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            StatRowItem(
                Icons.AutoMirrored.Filled.MenuBook,
                MaterialTheme.colorScheme.primary,
                stringResource(R.string.currently_reading),
                reading.toString()
            )
            HorizontalDivider(
                Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            StatRowItem(
                Icons.Default.CheckCircle,
                SuccessColor,
                stringResource(R.string.books_finished),
                finished.toString()
            )
            HorizontalDivider(
                Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            StatRowItem(
                Icons.Default.Bookmark,
                RatingStarColor,
                stringResource(R.string.want_to_read),
                wantToRead.toString()
            )
            HorizontalDivider(
                Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            StatRowItem(
                Icons.Default.AutoStories,
                MaterialTheme.colorScheme.onSurfaceVariant,
                stringResource(R.string.total_in_library),
                total.toString()
            )
        }
    }
}

@Composable
private fun StatRowItem(icon: ImageVector, iconTint: Color, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconTint.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun SignOutButton(onSignOut: () -> Unit) {
    OutlinedButton(
        onClick = onSignOut, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
    ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.sign_out))
    }
}

@Composable
private fun IconButton24(icon: ImageVector, tint: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Profile – Light", showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenLightPreview() {
    HonariTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Text(
                "Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 20.dp, bottom = 28.dp)
            )
            ProfileAvatar("Bianca Ionescu", "bianca@honari.app", null, System.currentTimeMillis())
            Spacer(Modifier.height(24.dp))
            ReadingGoalCard(booksRead = 8, goal = 24, onUpdateGoal = {})
            Spacer(Modifier.height(16.dp))
            ReadingProgressCard(reading = 2, finished = 8, total = 15)
            Spacer(Modifier.height(16.dp))
            StatsCard(reading = 2, finished = 8, wantToRead = 5, total = 15)
            Spacer(Modifier.height(16.dp))
            ThemeToggleCard(themePreference = ThemePreference.SYSTEM, onThemeChange = {})
            Spacer(Modifier.height(24.dp))
            SignOutButton(onSignOut = {})
        }
    }
}

@Preview(
    name = "Profile – Dark", showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ProfileScreenDarkPreview() {
    HonariTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Text(
                "Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 20.dp, bottom = 28.dp)
            )
            ProfileAvatar("Bianca Ionescu", "bianca@honari.app", null, System.currentTimeMillis())
            Spacer(Modifier.height(24.dp))
            ReadingGoalCard(booksRead = 24, goal = 24, onUpdateGoal = {})
            Spacer(Modifier.height(16.dp))
            ReadingProgressCard(reading = 2, finished = 24, total = 26)
            Spacer(Modifier.height(16.dp))
            StatsCard(reading = 2, finished = 24, wantToRead = 5, total = 31)
            Spacer(Modifier.height(16.dp))
            ThemeToggleCard(themePreference = ThemePreference.DARK, onThemeChange = {})
            Spacer(Modifier.height(24.dp))
            SignOutButton(onSignOut = {})
        }
    }
}

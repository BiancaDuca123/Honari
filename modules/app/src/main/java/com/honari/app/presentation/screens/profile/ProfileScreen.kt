package com.honari.app.presentation.screens.profile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.honari.app.AppViewModel
import com.honari.app.BuildConfig
import com.honari.app.domain.model.Book
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal

private const val DEFAULT_USERNAME = "reader"
private const val DEFAULT_DISPLAY_NAME = "Honari Reader"
private const val DEFAULT_BIO = "Book enthusiast · Reading is life"
private const val HEADER_GRADIENT_MID_ALPHA = 0.9f
private const val HEADER_GRADIENT_END_ALPHA = 0.75f
private val AvatarSize = 92.dp

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as ComponentActivity
    val appViewModel: AppViewModel = hiltViewModel(activity)
    val username = uiState.user?.email?.substringBefore('@').orEmpty().ifEmpty { DEFAULT_USERNAME }

    ProfileContent(
        uiState = uiState,
        username = username,
        onToggleDarkMode = { enabled ->
            viewModel.setDarkMode(enabled)
            appViewModel.setDarkMode(enabled)
        },
        onLogout = viewModel::logout,
    )
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    username: String,
    onToggleDarkMode: (Boolean) -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        ProfileHeader(
            photoUrl = uiState.user?.photoUrl,
            displayName = uiState.user?.displayName ?: DEFAULT_DISPLAY_NAME,
            username = username,
            email = uiState.user?.email.orEmpty(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        StatsRow(
            totalRead = uiState.totalRead,
            wantToRead = uiState.wantToRead,
            totalBooks = uiState.allBooks.size,
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (uiState.allBooks.isNotEmpty()) {
            SectionLabel(text = "My Books")
            Spacer(modifier = Modifier.height(10.dp))
            BooksRow(books = uiState.allBooks)
            Spacer(modifier = Modifier.height(20.dp))
        }
        SectionLabel(text = "Appearance")
        Spacer(modifier = Modifier.height(8.dp))
        SettingsCard {
            ToggleRow(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                subtitle = "Switch to a darker reading theme",
                checked = uiState.isDarkMode,
                onCheckedChange = onToggleDarkMode,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        SectionLabel(text = "Reading")
        Spacer(modifier = Modifier.height(8.dp))
        SettingsCard {
            InfoRow(
                icon = Icons.Default.CheckCircle,
                title = "Books Read",
                value = uiState.totalRead.toString(),
                iconTint = PrimaryTeal,
            )
            SettingsDivider()
            InfoRow(
                icon = Icons.Default.BookmarkBorder,
                title = "Wish List",
                value = uiState.wantToRead.toString(),
                iconTint = BrownHeadline,
            )
            SettingsDivider()
            InfoRow(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                title = "Total in Library",
                value = uiState.allBooks.size.toString(),
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        SectionLabel(text = "About")
        Spacer(modifier = Modifier.height(8.dp))
        SettingsCard {
            InfoRow(
                icon = Icons.Default.Star,
                title = "App Version",
                value = BuildConfig.VERSION_NAME,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SettingsDivider()
            InfoRow(
                icon = Icons.Default.Info,
                title = "Honari",
                value = "Book discovery & tracking",
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        SectionLabel(text = "Account")
        Spacer(modifier = Modifier.height(8.dp))
        SettingsCard {
            ActionRow(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Sign Out",
                iconTint = ErrorRed,
                textColor = ErrorRed,
                onClick = onLogout,
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileHeader(
    photoUrl: String?,
    displayName: String,
    username: String,
    email: String,
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val gradient = Brush.verticalGradient(
        listOf(
            PrimaryTeal,
            PrimaryTeal.copy(alpha = HEADER_GRADIENT_MID_ALPHA),
            PrimaryTeal.copy(alpha = HEADER_GRADIENT_END_ALPHA),
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(top = statusBarPadding + 20.dp, start = 24.dp, end = 24.dp, bottom = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ProfileAvatar(photoUrl = photoUrl)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CardWhite,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "@$username",
                style = MaterialTheme.typography.bodyMedium,
                color = CardWhite.copy(alpha = 0.85f),
            )
            if (email.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = CardWhite.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = DEFAULT_BIO,
                style = MaterialTheme.typography.bodySmall,
                color = CardWhite.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}

@Composable
private fun ProfileAvatar(photoUrl: String?) {
    Box(
        modifier = Modifier
            .size(AvatarSize)
            .border(width = 3.dp, color = CardWhite, shape = CircleShape)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        if (photoUrl.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .size(AvatarSize - 6.dp)
                    .clip(CircleShape)
                    .background(CardWhite.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = CardWhite,
                    modifier = Modifier.size(48.dp),
                )
            }
        } else {
            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                modifier = Modifier.size(AvatarSize).clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun StatsRow(totalRead: Int, wantToRead: Int, totalBooks: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatItem(count = totalRead, label = "Read", color = PrimaryTeal)
            VerticalDivider(modifier = Modifier.height(36.dp), color = MaterialTheme.colorScheme.outlineVariant)
            StatItem(count = wantToRead, label = "Wish List", color = BrownHeadline)
            VerticalDivider(modifier = Modifier.height(36.dp), color = MaterialTheme.colorScheme.outlineVariant)
            StatItem(count = totalBooks, label = "Total", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun StatItem(count: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BooksRow(books: List<Book>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(items = books, key = { it.id }) { book ->
            BookCoverThumb(book = book)
        }
    }
}

@Composable
private fun BookCoverThumb(book: Book) {
    if (book.imageUrl.isEmpty()) {
        Box(
            modifier = Modifier
                .size(width = 72.dp, height = 108.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryTeal.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = PrimaryTeal)
        }
    } else {
        AsyncImage(
            model = book.imageUrl,
            contentDescription = book.title,
            modifier = Modifier.size(width = 72.dp, height = 108.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = androidx.compose.ui.unit.TextUnit(1.2f, androidx.compose.ui.unit.TextUnitType.Sp),
        modifier = Modifier.padding(horizontal = 20.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    )
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = CardWhite, checkedTrackColor = PrimaryTeal),
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, title: String, value: String, iconTint: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    iconTint: Color,
    textColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = textColor)
    }
}

package com.honari.app.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.honari.app.domain.model.Book
import com.honari.app.presentation.theme.BackgroundBeige
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.TextPrimary
import com.honari.app.presentation.theme.TextSecondary

private val HeaderHeight = 200.dp
private val HeaderPanelHeight = 150.dp
private val AvatarSize = 120.dp
private val AvatarOffset = 50.dp

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val username = uiState.user?.email?.substringBefore('@').orEmpty().ifEmpty { "reader" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileHeader(photoUrl = uiState.user?.photoUrl)
        Spacer(modifier = Modifier.height(72.dp))
        Text(
            text = uiState.user?.displayName ?: "Honari Reader",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "@$username",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Book enthusiast",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Discovering memorable stories, collecting favorites, and sharing every " +
                "great read along the way.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 28.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        StatsRow(
            totalRead = uiState.totalRead,
            wantToRead = uiState.wantToRead,
            totalBooks = uiState.allBooks.size,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BooksSection(allBooks = uiState.allBooks)
        Spacer(modifier = Modifier.height(16.dp))
        SettingsSection(
            isDarkMode = uiState.isDarkMode,
            onToggleDarkMode = viewModel::toggleDarkMode,
            onLogout = viewModel::logout,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileHeader(photoUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderPanelHeight)
                .background(PrimaryTeal),
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = CardWhite,
                )
            }
        }
        ProfileAvatar(
            photoUrl = photoUrl,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = AvatarOffset),
        )
    }
}

@Composable
private fun ProfileAvatar(photoUrl: String?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(AvatarSize)
            .clip(CircleShape)
            .background(CardWhite),
        contentAlignment = Alignment.Center,
    ) {
        if (photoUrl.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .size(AvatarSize - 10.dp)
                    .clip(CircleShape)
                    .background(PrimaryTeal.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(48.dp),
                )
            }
        } else {
            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(AvatarSize - 10.dp)
                    .clip(CircleShape),
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
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatItem(count = totalRead, label = "Read")
            VerticalDivider(modifier = Modifier.height(32.dp))
            StatItem(count = wantToRead, label = "Want to Read")
            VerticalDivider(modifier = Modifier.height(32.dp))
            StatItem(count = totalBooks, label = "Total")
        }
    }
}

@Composable
private fun StatItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = BrownHeadline,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
    }
}

@Composable
private fun BooksSection(allBooks: List<Book>) {
    Text(
        text = "My Books",
        style = MaterialTheme.typography.headlineSmall,
        color = BrownHeadline,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    )
    Spacer(modifier = Modifier.height(8.dp))
    if (allBooks.isEmpty()) {
        Text(
            text = "Your publications will appear here once you add books to your collection.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    } else {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items = allBooks, key = { it.id }) { book ->
                BookCoverCard(book = book)
            }
        }
    }
}

@Composable
private fun BookCoverCard(book: Book) {
    if (book.imageUrl.isEmpty()) {
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryTeal.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = PrimaryTeal,
            )
        }
    } else {
        AsyncImage(
            model = book.imageUrl,
            contentDescription = book.title,
            modifier = Modifier
                .size(width = 80.dp, height = 120.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun SettingsSection(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onLogout: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            SettingToggleRow(
                checked = isDarkMode,
                onCheckedChange = { onToggleDarkMode() },
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed,
                    contentColor = CardWhite,
                ),
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Logout")
            }
        }
    }
}

@Composable
private fun SettingToggleRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.DarkMode,
            contentDescription = null,
            tint = PrimaryTeal,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Dark Mode",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
            )
            Text(
                text = "Switch between light and dark reading moods.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

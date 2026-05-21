package com.honari.app.presentation.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.presentation.theme.AccentAmber
import com.honari.app.presentation.theme.AccentCoral
import com.honari.app.presentation.theme.AccentIndigo
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.PrimaryTealDark
import com.honari.app.presentation.theme.StarGold
import java.util.Locale

private const val BOOK_ASPECT_RATIO = 0.67f
private const val EMPTY_ICON_SIZE = 72
private const val STATUS_BADGE_ALPHA = 0.15f
private const val GRID_COLUMNS = 2
private const val STAT_BADGE_ALPHA = 0.18f

@Composable
internal fun LibraryHeader(bookCount: Int, topPadding: Int) {
    val gradient = Brush.verticalGradient(listOf(PrimaryTeal, PrimaryTealDark))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(top = topPadding.dp + 20.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "My Library",
                        style = MaterialTheme.typography.displaySmall,
                        color = CardWhite,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (bookCount == 0) "Start adding books!" else "$bookCount books collected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CardWhite.copy(alpha = 0.8f),
                    )
                }
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = CardWhite.copy(alpha = 0.15f),
                    modifier = Modifier.size(56.dp),
                )
            }
        }
    }
}

@Composable
internal fun LibraryContent(
    modifier: Modifier = Modifier,
    uiState: LibraryUiState,
    onRemoveBook: (String) -> Unit,
    onBookClick: (String) -> Unit,
) {
    Box(modifier = modifier) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.allBooks.isEmpty() -> EmptyState(
                icon = Icons.Default.AutoStories,
                message = "Start scanning books\nto build your library.",
                tint = PrimaryTeal,
            )
            uiState.displayedBooks.isEmpty() -> EmptyState(
                icon = Icons.Default.Bookmark,
                message = "No books here yet.\nAdd some from Explore!",
                tint = AccentIndigo,
            )
            else -> BookGrid(
                books = uiState.displayedBooks,
                onRemoveBook = onRemoveBook,
                onBookClick = onBookClick,
            )
        }
    }
}

@Composable
private fun BookGrid(
    books: List<Book>,
    onRemoveBook: (String) -> Unit,
    onBookClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(books, key = { it.id }) { book ->
            BookGridCard(
                book = book,
                onRemove = { onRemoveBook(book.id) },
                onClick = { onBookClick(book.id) },
            )
        }
    }
}

@Composable
private fun BookGridCard(book: Book, onRemove: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            Box {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxWidth().aspectRatio(BOOK_ASPECT_RATIO),
                    contentScale = ContentScale.Crop,
                )
                StatusChip(
                    status = book.libraryStatus,
                    modifier = Modifier.align(Alignment.TopStart).padding(6.dp),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(onClick = onRemove),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = book.authors.firstOrNull().orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (book.averageRating > 0f) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarGold,
                            modifier = Modifier.size(11.dp),
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f", book.averageRating),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun StatusChip(status: ReadingStatus?, modifier: Modifier = Modifier) {
    val (label, color, icon) = when (status) {
        ReadingStatus.READ -> Triple("Read", PrimaryTeal, Icons.Default.DoneAll)
        ReadingStatus.WANT_TO_READ -> Triple("Want", AccentCoral, Icons.Default.Bookmark)
        else -> Triple("Saved", AccentIndigo, Icons.Default.AutoStories)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.55f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(9.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryTeal)
    }
}

@Composable
private fun EmptyState(icon: ImageVector, message: String, tint: Color) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint.copy(alpha = 0.35f),
                modifier = Modifier.size(EMPTY_ICON_SIZE.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        }
    }
}

internal fun buildFolders(books: List<Book>): List<LibraryFolder> = listOf(
    LibraryFolder(
        name = LibraryFilter.CONTINUE_READING.title,
        filter = LibraryFilter.CONTINUE_READING,
        books = books.filter { it.libraryStatus == ReadingStatus.READ },
    ),
    LibraryFolder(
        name = LibraryFilter.WISH_LIST.title,
        filter = LibraryFilter.WISH_LIST,
        books = books.filter { it.libraryStatus == ReadingStatus.WANT_TO_READ },
    ),
    LibraryFolder(
        name = LibraryFilter.ALL_BOOKS.title,
        filter = LibraryFilter.ALL_BOOKS,
        books = books,
    ),
)

internal fun buildFolderSubtitle(books: List<Book>): String {
    if (books.isEmpty()) return "0 books"
    val previewTitles = books.take(2).joinToString { it.title }
    return "${books.size} books · $previewTitles"
}

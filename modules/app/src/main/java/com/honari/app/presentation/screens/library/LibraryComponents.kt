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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal

private const val BOOK_ASPECT_RATIO = 0.67f
private const val COVER_WIDTH = 72
private const val HEADER_GRADIENT_ALPHA = 0.85f
private const val EMPTY_ICON_SIZE = 64
private const val STATUS_BADGE_ALPHA = 0.15f

@Composable
internal fun LibraryHeader(bookCount: Int, topPadding: Int) {
    val gradient = Brush.verticalGradient(listOf(PrimaryTeal, PrimaryTeal.copy(alpha = HEADER_GRADIENT_ALPHA)))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(top = topPadding.dp + 20.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
    ) {
        Column {
            Text(
                text = "My Library",
                style = MaterialTheme.typography.headlineLarge,
                color = CardWhite,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (bookCount == 0) "No books yet" else "$bookCount book${if (bookCount == 1) "" else "s"}",
                style = MaterialTheme.typography.bodyMedium,
                color = CardWhite.copy(alpha = 0.8f),
            )
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
                message = "Start scanning books\nto build your library.",
            )
            uiState.displayedBooks.isEmpty() -> EmptyState(
                message = "No books here yet.\nAdd some from Explore!",
            )
            else -> BookList(
                books = uiState.displayedBooks,
                onRemoveBook = onRemoveBook,
                onBookClick = onBookClick,
            )
        }
    }
}

@Composable
private fun BookList(
    books: List<Book>,
    onRemoveBook: (String) -> Unit,
    onBookClick: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        items(books, key = { it.id }) { book ->
            SwipeableBookRow(
                book = book,
                onRemove = { onRemoveBook(book.id) },
                onClick = { onBookClick(book.id) },
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            )
        }
    }
}

@Composable
private fun SwipeableBookRow(book: Book, onRemove: () -> Unit, onClick: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState()
    LaunchedEffect(dismissState.settledValue) {
        if (dismissState.settledValue == SwipeToDismissBoxValue.EndToStart) onRemove()
    }
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ErrorRed)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = "Remove",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }
        },
    ) {
        BookRow(book = book, onClick = onClick)
    }
}

@Composable
private fun BookRow(book: Book, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Box(modifier = Modifier.width(COVER_WIDTH.dp).aspectRatio(BOOK_ASPECT_RATIO)) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = book.authors.firstOrNull() ?: "Unknown author",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(6.dp))
            StatusBadge(status = book.libraryStatus)
        }
    }
}

@Composable
private fun StatusBadge(status: ReadingStatus?) {
    val (label, color) = when (status) {
        ReadingStatus.SAVED -> "Saved" to MaterialTheme.colorScheme.onSurfaceVariant
        ReadingStatus.READ -> "Read" to PrimaryTeal
        ReadingStatus.WANT_TO_READ -> "Wish List" to BrownHeadline
        null -> "Saved" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = STATUS_BADGE_ALPHA),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryTeal)
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = PrimaryTeal.copy(alpha = 0.4f),
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

package com.honari.app.presentation.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.TextPrimary
import com.honari.app.presentation.theme.TextSecondary

@Composable
internal fun LibraryActionButtonContent(label: String) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = CardWhite,
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = CardWhite,
        )
    }
}

@Composable
internal fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = BrownHeadline,
        )
        Spacer(modifier = Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BrownHeadline.copy(alpha = 0.2f),
        )
    }
}

@Composable
internal fun LibraryContent(
    modifier: Modifier = Modifier,
    uiState: LibraryUiState,
    folders: List<LibraryFolder>,
    onSelectFilter: (LibraryFilter) -> Unit,
) {
    Box(modifier = modifier) {
        when {
            uiState.isLoading -> LoadingLibraryState()
            uiState.allBooks.isEmpty() -> EmptyLibraryState(
                message = "Start scanning books to build a collection that feels like home.",
            )
            uiState.selectedFilter == null -> FolderList(
                folders = folders,
                onSelectFilter = onSelectFilter,
            )
            uiState.displayedBooks.isEmpty() -> EmptyLibraryState(
                message = "No books in ${uiState.selectedFilter.title} yet.",
            )
            else -> FilteredBooksList(books = uiState.displayedBooks)
        }
    }
}

@Composable
private fun LoadingLibraryState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryTeal)
    }
}

@Composable
private fun EmptyLibraryState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}

@Composable
private fun FilteredBooksList(books: List<Book>) {
    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(books, key = { it.id }) { book ->
            LibraryBookRow(book = book)
        }
    }
}

@Composable
private fun LibraryBookRow(book: Book) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThumbnailImage(imageUrl = book.imageUrl)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.authors.joinToString().ifEmpty { "Unknown author" },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = book.libraryStatus.toDisplayName(),
                style = MaterialTheme.typography.labelLarge,
                color = PrimaryTeal,
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
    if (books.isEmpty()) {
        return "0 books"
    }
    val previewTitles = books.take(2).joinToString { it.title }
    return "${books.size} books · $previewTitles"
}

private fun ReadingStatus?.toDisplayName(): String = when (this) {
    ReadingStatus.READ -> "Read"
    ReadingStatus.WANT_TO_READ -> "Wish List"
    null -> "Saved"
}

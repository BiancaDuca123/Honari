package com.honari.app.presentation.screens.book

import android.content.res.Configuration
import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.honari.app.R
import com.honari.app.domain.model.Book
import com.honari.app.presentation.theme.HonariTheme
import com.honari.app.presentation.theme.RatingStarColor
import com.honari.app.presentation.theme.SuccessColor
import com.honari.app.presentation.theme.TertiaryTextColor

@Composable
fun BookDetailScreen(bookId: String, onBack: () -> Unit, viewModel: BookDetailViewModel) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(bookId) { viewModel.loadBook(bookId) }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onBack, modifier = Modifier.padding(top = 4.dp, start = 4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onBackground)
            }
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                state.error != null && state.book == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                }
                state.book != null -> BookDetailContent(state = state, onAddToLibrary = viewModel::addToLibrary)
            }
        }
        if (state.error != null && state.book != null) {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                action = { TextButton(onClick = { viewModel.clearError() }) { Text(stringResource(R.string.dismiss)) } },
            ) { Text(state.error!!) }
        }
    }
}

@Composable
private fun BookDetailContent(state: BookDetailUiState, onAddToLibrary: () -> Unit) {
    val book = state.book ?: return
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        BookCoverImage(imageUrl = book.imageUrl, title = book.title)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            BookMetaHeader(book = book, isInLibrary = state.isInLibrary, onAddToLibrary = onAddToLibrary)
            if (book.description.isNotBlank()) {
                Spacer(Modifier.height(20.dp))
                Text(stringResource(R.string.about_book), style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(8.dp))
                val cleanDescription = remember(book.description) {
                    Html.fromHtml(book.description, Html.FROM_HTML_MODE_COMPACT).toString().trim()
                }
                Text(cleanDescription, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            val meta = buildList {
                if (book.publisher.isNotBlank()) add(book.publisher)
                if (book.pageCount > 0) add("${book.pageCount} pages")
                if (book.publishedDate.isNotBlank()) add(book.publishedDate)
            }
            if (meta.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(meta.joinToString(" · "), style = MaterialTheme.typography.bodySmall, color = TertiaryTextColor)
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun BookCoverImage(imageUrl: String, title: String) {
    Box(modifier = Modifier.fillMaxWidth().height(280.dp), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = imageUrl, contentDescription = title, contentScale = ContentScale.Fit,
            modifier = Modifier.height(240.dp).clip(RoundedCornerShape(12.dp)),
        )
    }
}

@Composable
private fun BookMetaHeader(book: Book, isInLibrary: Boolean, onAddToLibrary: () -> Unit) {
    book.category?.let { cat ->
        SuggestionChip(onClick = {}, label = { Text(cat, style = MaterialTheme.typography.labelSmall) })
        Spacer(Modifier.height(8.dp))
    }
    Text(book.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(4.dp))
    Text(book.author, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    if (book.rating > 0f) {
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("★ ${String.format("%.1f", book.rating)}", color = RatingStarColor, fontWeight = FontWeight.SemiBold)
            if (book.readers > 0) Text("  ·  ${book.readers} ratings",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = { if (!isInLibrary) onAddToLibrary() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (isInLibrary) SuccessColor else MaterialTheme.colorScheme.primary),
    ) {
        if (isInLibrary) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.in_library))
        } else {
            Text(stringResource(R.string.add_to_library))
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

private val previewBook = Book(
    id = "1", title = "The Great Gatsby", author = "F. Scott Fitzgerald",
    rating = 4.2f, readers = 12500, category = "Fiction",
    description = "A novel set in the Jazz Age that follows the mysterious Jay Gatsby and his obsession with the beautiful Daisy Buchanan.",
    publisher = "Scribner", pageCount = 180, publishedDate = "1925"
)

@Preview(name = "Book Detail – Light", showBackground = true, showSystemUi = true)
@Composable
private fun BookDetailLightPreview() {
    HonariTheme(darkTheme = false) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Column(Modifier.fillMaxSize()) {
                IconButton(onClick = {}, modifier = Modifier.padding(top = 4.dp, start = 4.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                }
                BookDetailContent(
                    state = BookDetailUiState(book = previewBook, isInLibrary = false),
                    onAddToLibrary = {}
                )
            }
        }
    }
}

@Preview(name = "Book Detail – Dark", showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookDetailDarkPreview() {
    HonariTheme(darkTheme = true) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Column(Modifier.fillMaxSize()) {
                IconButton(onClick = {}, modifier = Modifier.padding(top = 4.dp, start = 4.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                }
                BookDetailContent(
                    state = BookDetailUiState(book = previewBook, isInLibrary = true),
                    onAddToLibrary = {}
                )
            }
        }
    }
}

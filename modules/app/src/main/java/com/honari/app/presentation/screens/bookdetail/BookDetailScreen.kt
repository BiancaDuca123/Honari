package com.honari.app.presentation.screens.bookdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal

private const val YEAR_LENGTH = 4
internal const val EMPTY_METRIC = "—"
private val HeaderHeight = 200.dp
private val CoverOffset = 56.dp
private val ActionShape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)

@Composable
fun BookDetailScreen(
    bookId: String,
    onBack: () -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when {
            uiState.isLoading && uiState.book == null -> LoadingState()
            uiState.book == null -> MissingBookState(bookId = bookId, onBack = onBack)
            else -> BookDetailContent(
                uiState = uiState,
                onBack = onBack,
                onSave = { viewModel.saveBook(ReadingStatus.WANT_TO_READ) },
                onReview = { viewModel.saveBook(ReadingStatus.READ) },
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        ) { data ->
            Snackbar(containerColor = ErrorRed, contentColor = CardWhite) {
                Text(text = data.visuals.message)
            }
        }
    }
}

@Composable
private fun BookDetailContent(
    uiState: BookDetailUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onReview: () -> Unit,
) {
    val book = uiState.book ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { BookHeroSection(book = book, onBack = onBack) }
        item { BookSummarySection(book = book) }
        item { BookActionSection(onSave = onSave, onReview = onReview) }
        item { ReviewsHeader(reviewCount = uiState.reviews.size) }
        if (uiState.reviews.isEmpty()) {
            item { EmptyReviewsState() }
        } else {
            items(uiState.reviews, key = { it.id }) { review ->
                ReviewCard(review = review)
            }
        }
    }
}

@Composable
private fun BookHeroSection(book: Book, onBack: () -> Unit) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight + CoverOffset + statusBarPadding),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderHeight + statusBarPadding)
                .background(PrimaryTeal),
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = statusBarPadding + 8.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CardWhite,
                )
            }
        }
        BookCover(
            imageUrl = book.imageUrl,
            title = book.title,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
        )
    }
}

@Composable
private fun BookSummarySection(book: Book) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineMedium,
            color = BrownHeadline,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = book.authors.firstOrNull().orEmpty().ifEmpty { "Unknown author" },
            style = MaterialTheme.typography.bodyLarge,
            color = PrimaryTeal,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = book.categories.firstOrNull().orEmpty().ifEmpty { "Literature" },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
        BookMetricsRow(book = book)
    }
}

@Composable
private fun BookMetricsRow(book: Book) {
    val year = book.publishedDate.take(YEAR_LENGTH).ifEmpty { EMPTY_METRIC }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        MetricItem(label = "Year", value = year)
        MetricItem(label = "Language", value = formatLanguage(book.language))
        MetricItem(label = "Pages", value = formatPages(book.pageCount))
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = PrimaryTeal,
        )
    }
}

@Composable
private fun BookActionSection(onSave: () -> Unit, onReview: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            shape = ActionShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryTeal,
                contentColor = CardWhite,
            ),
        ) {
            Text(text = "Save")
        }
        OutlinedButton(
            onClick = onReview,
            modifier = Modifier.weight(1f),
            shape = ActionShape,
            border = BorderStroke(1.dp, BrownHeadline),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BrownHeadline,
            ),
        ) {
            Text(text = "Review")
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
private fun MissingBookState(bookId: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "We couldn't find book $bookId.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
        ) {
            Text(text = "Go Back")
        }
    }
}

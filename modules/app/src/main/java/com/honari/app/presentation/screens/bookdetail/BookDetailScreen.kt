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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.model.Review
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.StarGold
import com.honari.app.presentation.theme.TextSecondary

private const val YEAR_LENGTH = 4
internal const val EMPTY_METRIC = "—"
private val HeaderHeight = 200.dp
private val CoverOffset = 56.dp
private val ActionShape = RoundedCornerShape(16.dp)
private const val MAX_RATING = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    onBack: () -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccess()
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
                onSaveToLibrary = { viewModel.saveBook(ReadingStatus.SAVED) },
                onSaveWishlist = { viewModel.saveBook(ReadingStatus.WANT_TO_READ) },
                onSaveRead = { viewModel.saveBook(ReadingStatus.READ) },
                onReview = { viewModel.setReviewSheetVisible(true) },
                onEditReview = { review -> viewModel.setReviewSheetVisible(true, review) },
                onDeleteReview = viewModel::deleteReview,
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

    if (uiState.showReviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setReviewSheetVisible(false) },
            sheetState = sheetState,
            containerColor = CardWhite,
        ) {
            ReviewSheet(
                uiState = uiState,
                isEditing = uiState.editingReviewId != null,
                onReviewChanged = viewModel::onReviewChanged,
                onSubmit = viewModel::submitReview,
                onDismiss = { viewModel.setReviewSheetVisible(false) },
            )
        }
    }
}

@Composable
private fun BookDetailContent(
    uiState: BookDetailUiState,
    onBack: () -> Unit,
    onSaveToLibrary: () -> Unit,
    onSaveWishlist: () -> Unit,
    onSaveRead: () -> Unit,
    onReview: () -> Unit,
    onEditReview: (Review) -> Unit,
    onDeleteReview: (String) -> Unit,
) {
    val book = uiState.book ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { BookHeroSection(book = book, onBack = onBack) }
        item { BookSummarySection(book = book) }
        item {
            BookActionSection(
                isInLibrary = uiState.isInLibrary,
                onSaveToLibrary = onSaveToLibrary,
                onSaveWishlist = onSaveWishlist,
                onSaveRead = onSaveRead,
                onReview = onReview,
            )
        }
        item { ReviewsHeader(reviewCount = uiState.reviews.size) }
        if (uiState.reviews.isEmpty()) {
            item { EmptyReviewsState() }
        } else {
            items(uiState.reviews, key = { it.id }) { review ->
                ReviewCard(
                    review = review,
                    isOwner = review.userId == uiState.currentUserId,
                    onEdit = { onEditReview(review) },
                    onDelete = { onDeleteReview(review.id) },
                )
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
            color = MaterialTheme.colorScheme.onBackground,
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
        Spacer(modifier = Modifier.height(10.dp))
        BookRatingRow(rating = book.averageRating, ratingsCount = book.ratingsCount)
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
private fun BookActionSection(
    isInLibrary: Boolean,
    onSaveToLibrary: () -> Unit,
    onSaveWishlist: () -> Unit,
    onSaveRead: () -> Unit,
    onReview: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (isInLibrary) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "In your library",
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryTeal,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onSaveWishlist,
                    modifier = Modifier.weight(1f),
                    shape = ActionShape,
                    border = BorderStroke(1.dp, PrimaryTeal),
                ) { Text("Wishlist", color = PrimaryTeal) }
                Button(
                    onClick = onSaveRead,
                    modifier = Modifier.weight(1f),
                    shape = ActionShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryTeal,
                        contentColor = CardWhite,
                    ),
                ) { Text("Mark Read") }
            }
        } else {
            Button(
                onClick = onSaveToLibrary,
                modifier = Modifier.fillMaxWidth(),
                shape = ActionShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryTeal,
                    contentColor = CardWhite,
                ),
            ) { Text("Add to Library") }
        }
        OutlinedButton(
            onClick = onReview,
            modifier = Modifier.fillMaxWidth(),
            shape = ActionShape,
            border = BorderStroke(1.dp, PrimaryTeal),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryTeal),
        ) { Text("Write a Review") }
    }
}

@Composable
private fun ReviewSheet(
    uiState: BookDetailUiState,
    isEditing: Boolean,
    onReviewChanged: (String, Float) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = if (isEditing) "Edit Review" else "Write a Review",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        StarRatingRow(
            rating = uiState.reviewRating,
            onRatingChanged = { onReviewChanged(uiState.reviewText, it) },
        )
        OutlinedTextField(
            value = uiState.reviewText,
            onValueChange = { onReviewChanged(it, uiState.reviewRating) },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            placeholder = { Text("Share your thoughts about this book...", color = TextSecondary) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = PrimaryTeal,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = ActionShape,
            ) { Text("Cancel") }
            Button(
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                shape = ActionShape,
                enabled = uiState.reviewText.isNotBlank() &&
                    uiState.reviewRating > 0f &&
                    !uiState.isSubmittingReview,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryTeal,
                    contentColor = CardWhite,
                ),
            ) {
                if (uiState.isSubmittingReview) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = CardWhite,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(if (isEditing) "Update" else "Submit")
                }
            }
        }
    }
}

@Composable
private fun StarRatingRow(rating: Float, onRatingChanged: (Float) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..MAX_RATING) {
            IconButton(
                onClick = { onRatingChanged(i.toFloat()) },
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "$i stars",
                    tint = StarGold,
                    modifier = Modifier.size(28.dp),
                )
            }
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

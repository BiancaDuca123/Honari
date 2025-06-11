package com.honari.app.presentation.screens.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.honari.app.R
import com.honari.app.domain.model.Book
import com.honari.app.presentation.theme.BackgroundColor
import com.honari.app.presentation.theme.PrimaryColor
import com.honari.app.presentation.theme.PrimaryTextColor
import com.honari.app.presentation.theme.RatingStarColor
import com.honari.app.presentation.theme.SecondaryColor
import com.honari.app.presentation.theme.SecondaryTextColor
import com.honari.app.presentation.theme.SuccessColor

/**
 * Book detail screen composable.
 * Displays detailed information about a specific book.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    navController: NavController,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        uiState.book?.let { book ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Book Header with Image
                BookDetailHeader(
                    book = book,
                    onBackClick = { navController.navigateUp() }
                )

                // Book Info
                BookDetailInfo(
                    book = book,
                    isInLibrary = uiState.isInLibrary,
                    onAddToLibraryClick = { viewModel.toggleLibrary() }
                )

                // About Section
                AboutBookSection(book = book)

                // Similar Books
                if (uiState.similarBooks.isNotEmpty()) {
                    SimilarBooksSection(
                        books = uiState.similarBooks,
                        onBookClick = { similarBook ->
                            navController.navigate(
                                com.honari.app.presentation.navigation.Screen.BookDetail
                                    .createRoute(similarBook.id)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * Book detail header with cover image.
 */
@Composable
private fun BookDetailHeader(
    book: Book,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        // Background Image with Gradient
        AsyncImage(
            model = book.imageUrl,
            contentDescription = book.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

/**
 * Book detail info section.
 */
@Composable
private fun BookDetailInfo(
    book: Book,
    isInLibrary: Boolean,
    onAddToLibraryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-32).dp),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Title and Author
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.by_author, book.author),
                style = MaterialTheme.typography.bodyLarge,
                color = SecondaryTextColor,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rating and Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = RatingStarColor
                    )
                    Text(
                        text = book.rating.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = PrimaryTextColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Readers
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SecondaryTextColor
                    )
                    Text(
                        text = stringResource(
                            R.string.readers_count,
                            book.readers.formatThousands()
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryTextColor
                    )
                }

                // Category
                book.category?.let { category ->
                    Surface(
                        color = SecondaryColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = category,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAddToLibraryClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isInLibrary) SuccessColor else PrimaryColor
                    )
                ) {
                    Icon(
                        imageVector = if (isInLibrary)
                            Icons.Default.CheckCircle else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isInLibrary)
                            "In Library" else stringResource(R.string.add_to_library),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                OutlinedButton(
                    onClick = { /* TODO: Share functionality */ },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * About book section.
 */
@Composable
private fun AboutBookSection(book: Book) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.about_book),
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = book.description,
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryTextColor,
            lineHeight = 24.sp
        )
    }
}

/**
 * Similar books section.
 */
@Composable
private fun SimilarBooksSection(
    books: List<Book>,
    onBookClick: (Book) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
    ) {
        Text(
            text = "Similar Books",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books) { book ->
                SimilarBookItem(
                    book = book,
                    onClick = { onBookClick(book) }
                )
            }
        }
    }
}

/**
 * Similar book item component.
 */
@Composable
private fun SimilarBookItem(
    book: Book,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Extension function to format numbers.
 */
private fun Int.formatThousands(): String {
    return if (this >= 1000) {
        String.format("%.1fk", this / 1000.0)
    } else {
        this.toString()
    }
}

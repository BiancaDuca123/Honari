package com.honari.app.presentation.screens.feed

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.honari.app.domain.model.Book
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.StarGold
import java.util.Locale

internal const val FEATURED_HEIGHT = 260
internal const val NEW_RELEASE_CARD_WIDTH = 130
internal const val PICKS_COVER_WIDTH = 76
internal const val PICKS_COVER_HEIGHT = 114

private const val BOOK_ASPECT_RATIO = 0.67f
private const val GRADIENT_START_ALPHA = 0f
private const val GRADIENT_END_ALPHA = 0.85f
private const val FEATURED_BADGE_LABEL = "Featured"
private const val FEATURED_CORNER = 20
private const val CARD_CORNER = 14
private const val PICKS_CORNER = 10

private val GENRES = listOf(
    "Fiction", "Fantasy", "Mystery", "Romance",
    "Sci-Fi", "History", "Biography", "Science",
)

internal fun filterByGenre(books: List<Book>, genre: String?): List<Book> {
    if (genre == null) return books
    return books.filter { book ->
        book.categories.any { it.contains(genre, ignoreCase = true) }
    }.ifEmpty { books }
}

@Composable
internal fun GenreChipRow(selectedGenre: String?, onGenreSelected: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 10.dp),
    ) {
        items(GENRES) { genre ->
            FilterChip(
                selected = selectedGenre == genre,
                onClick = { onGenreSelected(genre) },
                label = {
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selectedGenre == genre) FontWeight.Bold else FontWeight.Normal,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryTeal,
                    selectedLabelColor = CardWhite,
                ),
            )
        }
    }
}

@Composable
internal fun FeaturedBookCard(book: Book, onClick: () -> Unit) {
    val gradient = Brush.verticalGradient(
        listOf(
            Color.Black.copy(alpha = GRADIENT_START_ALPHA),
            Color.Black.copy(alpha = GRADIENT_END_ALPHA),
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(FEATURED_HEIGHT.dp)
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(FEATURED_CORNER.dp))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = book.imageUrl,
            contentDescription = book.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(modifier = Modifier.fillMaxSize().background(gradient))
        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(14.dp),
            shape = RoundedCornerShape(20.dp),
            color = PrimaryTeal.copy(alpha = 0.9f),
        ) {
            Text(
                text = FEATURED_BADGE_LABEL,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = CardWhite,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = book.authors.firstOrNull().orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                fontStyle = FontStyle.Italic,
            )
            if (book.averageRating > 0f) {
                Spacer(modifier = Modifier.height(6.dp))
                InlineRatingRow(rating = book.averageRating, count = book.ratingsCount)
            }
        }
    }
}

@Composable
internal fun HorizontalBooksRow(books: List<Book>, onBookClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(books, key = { it.id }) { book ->
            NewReleaseCard(book = book, onClick = { onBookClick(book.id) })
        }
    }
}

@Composable
private fun NewReleaseCard(book: Book, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(NEW_RELEASE_CARD_WIDTH.dp)
            .clickable(onClick = onClick),
    ) {
        Card(
            shape = RoundedCornerShape(CARD_CORNER.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Box(
                modifier = Modifier
                    .width(NEW_RELEASE_CARD_WIDTH.dp)
                    .aspectRatio(BOOK_ASPECT_RATIO),
            ) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = book.title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = book.authors.firstOrNull().orEmpty(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (book.averageRating > 0f) {
            Spacer(modifier = Modifier.height(3.dp))
            InlineRatingRow(rating = book.averageRating, count = book.ratingsCount)
        }
    }
}

@Composable
internal fun TopPicksListItem(book: Book, showDivider: Boolean, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Card(
                shape = RoundedCornerShape(PICKS_CORNER.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Box(
                    modifier = Modifier
                        .width(PICKS_COVER_WIDTH.dp)
                        .height(PICKS_COVER_HEIGHT.dp),
                ) {
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.authors.firstOrNull().orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (book.categories.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PrimaryTeal.copy(alpha = 0.12f),
                    ) {
                        Text(
                            text = book.categories.first().take(20),
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryTeal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (book.averageRating > 0f) {
                        InlineRatingRow(rating = book.averageRating, count = book.ratingsCount)
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    if (book.pageCount > 0) {
                        Text(
                            text = "${book.pageCount} pages",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            )
        }
    }
}

@Composable
internal fun RatingRow(rating: Float, count: Int = 0) {
    InlineRatingRow(rating = rating, count = count)
}

@Composable
private fun InlineRatingRow(rating: Float, count: Int = 0) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = StarGold,
            modifier = Modifier.size(13.dp),
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = buildString {
                append(String.format(Locale.US, "%.1f", rating))
                if (count > 0) append(" ($count)")
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

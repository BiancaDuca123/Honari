package com.honari.app.presentation.screens.feed

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.honari.app.domain.model.Book
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.StarGold
import java.util.Locale

internal const val FEATURED_HEIGHT = 220
internal const val COMPACT_CARD_WIDTH = 120
internal const val COMPACT_CARD_HEIGHT = 160
internal const val BOOK_CARD_IMAGE_HEIGHT = 200

private const val GRADIENT_BLACK_ALPHA = 0.75f
private val GENRES = listOf(
    "Fiction",
    "Fantasy",
    "Mystery",
    "Romance",
    "Sci-Fi",
    "History",
    "Biography",
    "Science",
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
        modifier = Modifier.padding(vertical = 12.dp),
    ) {
        items(GENRES) { genre ->
            FilterChip(
                selected = selectedGenre == genre,
                onClick = { onGenreSelected(genre) },
                label = { Text(text = genre, style = MaterialTheme.typography.labelMedium) },
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
        listOf(Color.Transparent, Color.Black.copy(alpha = GRADIENT_BLACK_ALPHA)),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(FEATURED_HEIGHT.dp)
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = book.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(modifier = Modifier.fillMaxSize().background(gradient))
        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            color = PrimaryTeal,
        ) {
            Text(
                text = "Featured",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = CardWhite,
            )
        }
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = book.authors.firstOrNull().orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
            )
        }
    }
}

@Composable
internal fun HorizontalBooksRow(books: List<Book>, onBookClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(books, key = { it.id }) { book ->
            CompactBookCard(book = book, onClick = { onBookClick(book.id) })
        }
    }
}

@Composable
private fun CompactBookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(COMPACT_CARD_WIDTH.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(COMPACT_CARD_WIDTH.dp)
                    .height(COMPACT_CARD_HEIGHT.dp),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = book.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
internal fun BookCardRow(pair: List<Book>, onBookClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        pair.forEach { book ->
            ExploreBookCard(
                book = book,
                onClick = { onBookClick(book.id) },
                modifier = Modifier.weight(1f),
            )
        }
        if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ExploreBookCard(book: Book, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(BOOK_CARD_IMAGE_HEIGHT.dp),
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = book.authors.firstOrNull().orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (book.averageRating > 0f) RatingRow(rating = book.averageRating)
            }
        }
    }
}

@Composable
internal fun RatingRow(rating: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = String.format(Locale.US, "%.1f", rating),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = StarGold,
            modifier = Modifier.size(12.dp),
        )
    }
}

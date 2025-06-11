package com.honari.app.presentation.screens.discover

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.Mood
import com.honari.app.presentation.navigation.Screen
import com.honari.app.presentation.theme.BackgroundColor
import com.honari.app.presentation.theme.HeroBackground
import com.honari.app.presentation.theme.MoodContemplative
import com.honari.app.presentation.theme.MoodDreamy
import com.honari.app.presentation.theme.MoodMelancholic
import com.honari.app.presentation.theme.MoodNostalgic
import com.honari.app.presentation.theme.MoodRomantic
import com.honari.app.presentation.theme.PrimaryTextColor
import com.honari.app.presentation.theme.RatingStarColor
import com.honari.app.presentation.theme.SecondaryTextColor
import com.honari.app.presentation.theme.SurfaceLight
import com.honari.app.presentation.theme.TertiaryTextColor
import com.honari.app.presentation.theme.TrendingColor
import java.util.Locale

/**
 * Main Discover screen composable.
 * Displays featured books, moods, and trending content.
 */
@Composable
fun DiscoverScreen(
    navController: NavController,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val moods = viewModel.getMoods()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // Header
        item {
            DiscoverHeader(
                onSearchClick = { /* TODO: Navigate to search */ }
            )
        }

        // Hero Section
        item {
            HeroSection()
        }

        // Mood Selection
        item {
            MoodSection(
                moods = moods,
                onMoodSelected = viewModel::onMoodSelected
            )
        }

        // Featured Books
        item {
            FeaturedBooksSection(
                books = uiState.featuredBooks,
                onBookClick = { book ->
                    navController.navigate(Screen.BookDetail.createRoute(book.id))
                }
            )
        }

        // Trending Books
        item {
            TrendingBooksSection(
                books = uiState.trendingBooks,
                onBookClick = { book ->
                    navController.navigate(Screen.BookDetail.createRoute(book.id))
                }
            )
        }

        // Quote of the Day
        item {
            QuoteSection()
        }
    }
}

/**
 * Header component with greeting and search button.
 */
@Composable
private fun DiscoverHeader(
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Honari",
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Where stories breathe",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryTextColor
            )
        }


        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(40.dp)
                .background(SurfaceLight, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = PrimaryTextColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Hero section with inspirational message.
 */
@Composable
private fun HeroSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HeroBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MoodDreamy
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Discover your next literary escape",
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryTextColor,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Curated recommendations based on your reading soul",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryTextColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Mood selection section.
 */
@Composable
private fun MoodSection(
    moods: List<Mood>,
    onMoodSelected: (Mood) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 40.dp)) {
        SectionHeader(
            title = "How are you feeling?",
            subtitle = "Let your mood guide your next read"
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(moods) { mood ->
                MoodCard(
                    mood = mood,
                    onClick = { onMoodSelected(mood) }
                )
            }
        }
    }
}

/**
 * Individual mood card component.
 */
@Composable
private fun MoodCard(
    mood: Mood,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(mood.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getMoodIcon(mood.iconName),
                        contentDescription = mood.name,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = mood.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = mood.color,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = mood.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Accent bar at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(mood.color)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Helper function to get mood icon based on name.
 */
@Composable
private fun getMoodIcon(iconName: String) = when (iconName) {
    "moon" -> Icons.Default.Nightlight
    "cloud" -> Icons.Default.Cloud
    "sunset" -> Icons.Default.WbSunny
    "coffee" -> Icons.Default.Coffee
    "heart" -> Icons.Default.Favorite
    else -> Icons.Default.AutoAwesome
}

/**
 * Featured books section.
 */
@Composable
private fun FeaturedBooksSection(
    books: List<Book>,
    onBookClick: (Book) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 40.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Nightlight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MoodDreamy
                )
                Text(
                    text = "Tonight's picks",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            TextButton(onClick = { /* TODO: Navigate to see all */ }) {
                Text("See all", color = SecondaryTextColor)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = SecondaryTextColor
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(books) { book ->
                FeaturedBookCard(
                    book = book,
                    onClick = { onBookClick(book) }
                )
            }
        }
    }
}

/**
 * Featured book card component.
 */
@Composable
private fun FeaturedBookCard(
    book: Book,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Rating badge
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = RatingStarColor
                        )
                        Text(
                            text = book.rating.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryTextColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mood tag
                    Surface(
                        color = getMoodColor(book.mood).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = book.mood,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = getMoodColor(book.mood),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "${book.readers.formatThousands()} readers",
                        style = MaterialTheme.typography.labelSmall,
                        color = TertiaryTextColor
                    )
                }
            }
        }
    }
}

/**
 * Trending books section.
 */
@Composable
private fun TrendingBooksSection(
    books: List<Book>,
    onBookClick: (Book) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 40.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = TrendingColor
                )
                Text(
                    text = "Trending now",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            TextButton(onClick = { /* TODO: Navigate to see all */ }) {
                Text("See all", color = SecondaryTextColor)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = SecondaryTextColor
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            books.take(2).forEach { book ->
                TrendingBookCard(
                    book = book,
                    onClick = { onBookClick(book) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Trending book card component.
 */
@Composable
private fun TrendingBookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Trend badge
                book.trend?.let { trend ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = TrendingColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = trend,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                book.category?.let { category ->
                    Text(
                        text = category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MoodDreamy,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = RatingStarColor
                    )
                    Text(
                        text = book.rating.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryTextColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Quote section component.
 */
@Composable
private fun QuoteSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HeroBackground),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 4.dp,
            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                colors = listOf(MoodRomantic, MoodRomantic)
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MoodRomantic
                )
                Text(
                    text = "QUOTE OF THE DAY",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryTextColor,
                    letterSpacing = 0.5.sp
                )
            }

            Text(
                text = "\"The books that the world calls immoral are books that show the world its own shame.\"",
                style = MaterialTheme.typography.bodyLarge,
                color = PrimaryTextColor,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "— Oscar Wilde",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryTextColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Section header component.
 */
@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor,
            fontWeight = FontWeight.SemiBold
        )
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryTextColor,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Helper function to get mood color.
 */
@Composable
private fun getMoodColor(moodName: String): Color = when (moodName) {
    "Melancholic" -> MoodMelancholic
    "Dreamy" -> MoodDreamy
    "Nostalgic" -> MoodNostalgic
    "Contemplative" -> MoodContemplative
    "Romantic" -> MoodRomantic
    else -> MoodDreamy
}

/**
 * Extension function to format numbers with thousands separator.
 */
private fun Int.formatThousands(): String {
    return if (this >= 1000) {
        String.format(Locale.US, "%.1fk", this / 1000.0)
    } else {
        this.toString()
    }
}

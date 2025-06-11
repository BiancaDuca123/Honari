package com.honari.app.presentation.screens.library

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.honari.app.domain.model.LibraryBook
import com.honari.app.domain.model.LibraryStats
import com.honari.app.domain.model.LibraryTab
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.presentation.components.shimmerEffect
import com.honari.app.presentation.navigation.Screen
import com.honari.app.presentation.theme.BackgroundColor
import com.honari.app.presentation.theme.ErrorColor
import com.honari.app.presentation.theme.HeroBackground
import com.honari.app.presentation.theme.MoodContemplative
import com.honari.app.presentation.theme.MoodDreamy
import com.honari.app.presentation.theme.MoodNostalgic
import com.honari.app.presentation.theme.PrimaryColor
import com.honari.app.presentation.theme.PrimaryTextColor
import com.honari.app.presentation.theme.RatingStarColor
import com.honari.app.presentation.theme.SecondaryTextColor
import com.honari.app.presentation.theme.SurfaceLight
import com.honari.app.presentation.theme.TertiaryTextColor
import com.honari.app.presentation.theme.TrendingColor

/**
 * Library screen composable with improved design.
 * Displays user's book collection with enhanced visuals.
 */
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(LibraryTab.ALL) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Header
        item {
            LibraryHeader(
                onSearchClick = { /* TODO: Navigate to search */ },
                onFilterClick = { /* TODO: Show filter options */ },
                onAddClick = { /* TODO: Add new book */ }
            )
        }

        // Library Stats with gradient background
        item {
            LibraryStatsSection(stats = uiState.stats)
        }

        // Tab Selection
        item {
            LibraryTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                tabCounts = uiState.tabCounts
            )
        }

        // Books List
        val books = when (selectedTab) {
            LibraryTab.ALL -> uiState.allBooks
            LibraryTab.READING -> uiState.currentlyReading
            LibraryTab.READ -> uiState.finishedBooks
            LibraryTab.TO_READ -> uiState.wantToRead
        }

        if (books.isEmpty() && !uiState.isLoading) {
            item {
                EmptyLibraryState(tab = selectedTab)
            }
        } else {
            items(books) { book ->
                LibraryBookCard(
                    book = book,
                    onClick = {
                        navController.navigate(Screen.BookDetail.createRoute(book.id))
                    }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

/**
 * Library header with improved design.
 */
@Composable
private fun LibraryHeader(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddClick: () -> Unit
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
                text = "My Library",
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your literary sanctuary",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryTextColor
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceLight, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = PrimaryTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryTextColor, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Book",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Library statistics section with gradient cards.
 */
@Composable
private fun LibraryStatsSection(stats: LibraryStats) {
    LazyRow(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
    ) {
        item {
            StatCard(
                value = stats.totalBooks.toString(),
                label = "Books",
                gradient = listOf(PrimaryColor, PrimaryColor.copy(alpha = 0.8f))
            )
        }
        item {
            StatCard(
                value = stats.booksThisYear.toString(),
                label = "This year",
                gradient = listOf(MoodDreamy, MoodDreamy.copy(alpha = 0.8f))
            )
        }
        item {
            StatCard(
                value = String.format("%.1f", stats.avgRating),
                label = "Avg rating",
                gradient = listOf(RatingStarColor, RatingStarColor.copy(alpha = 0.8f))
            )
        }
        item {
            StatCard(
                value = stats.pagesRead.toString(),
                label = "Pages read",
                gradient = listOf(TrendingColor, TrendingColor.copy(alpha = 0.8f))
            )
        }
    }
}

/**
 * Individual stat card with gradient background.
 */
@Composable
private fun StatCard(
    value: String,
    label: String,
    gradient: List<Color>
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(gradient)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

/**
 * Tab row for filtering books.
 */
@Composable
private fun LibraryTabRow(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit,
    tabCounts: Map<LibraryTab, Int>
) {
    LazyRow(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
    ) {
        items(LibraryTab.values().toList()) { tab ->
            LibraryTabItem(
                tab = tab,
                isSelected = selectedTab == tab,
                count = tabCounts[tab] ?: 0,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

/**
 * Individual tab item with animation.
 */
@Composable
private fun LibraryTabItem(
    tab: LibraryTab,
    isSelected: Boolean,
    count: Int,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryTextColor else SurfaceLight,
        animationSpec = tween(300),
        label = "tab_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else SecondaryTextColor,
        animationSpec = tween(300),
        label = "tab_content"
    )

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = tab.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected)
                    Color.White.copy(alpha = 0.3f)
                else
                    Color.Black.copy(alpha = 0.1f)
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Enhanced book card with status indicators.
 */
@Composable
private fun LibraryBookCard(
    book: LibraryBook,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book Cover with loading animation
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect(),
                    contentScale = ContentScale.Crop
                )

                // Status overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(
                            color = when (book.status) {
                                ReadingStatus.READING -> MoodDreamy
                                ReadingStatus.FINISHED -> TrendingColor
                                ReadingStatus.WANT_TO_READ -> MoodNostalgic
                            },
                            shape = CircleShape
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = when (book.status) {
                            ReadingStatus.READING -> Icons.Default.Timer
                            ReadingStatus.FINISHED -> Icons.Default.CheckCircle
                            ReadingStatus.WANT_TO_READ -> Icons.Default.Bookmark
                        },
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.White
                    )
                }
            }

            // Book Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Title and Author
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryTextColor
                )

                // Progress or Rating
                when (book.status) {
                    ReadingStatus.READING -> {
                        ReadingProgress(progress = book.progress ?: 0)
                    }
                    ReadingStatus.FINISHED -> {
                        book.rating?.let { rating ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = RatingStarColor
                                )
                                Text(
                                    text = rating.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PrimaryTextColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    ReadingStatus.WANT_TO_READ -> {
                        // Tags
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            book.tags.take(2).forEach { tag ->
                                TagChip(tag = tag)
                            }
                        }
                    }
                }

                // Date added
                Text(
                    text = "Added ${book.dateAdded}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TertiaryTextColor
                )
            }
        }
    }
}

/**
 * Reading progress indicator.
 */
@Composable
private fun ReadingProgress(progress: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryTextColor
            )
            Text(
                text = "$progress%",
                style = MaterialTheme.typography.labelMedium,
                color = MoodDreamy,
                fontWeight = FontWeight.SemiBold
            )
        }

        val animatedProgress by animateFloatAsState(
            targetValue = progress / 100f,
            animationSpec = tween(1000),
            label = "progress"
        )

        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MoodDreamy,
            trackColor = SurfaceLight
        )
    }
}

/**
 * Tag chip component.
 */
@Composable
private fun TagChip(tag: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = HeroBackground
    ) {
        Text(
            text = tag,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = SecondaryTextColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Empty state for library tabs.
 */
@Composable
private fun EmptyLibraryState(tab: LibraryTab) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (icon, message) = when (tab) {
            LibraryTab.ALL -> Icons.Default.Bookmark to "Your library is empty"
            LibraryTab.READING -> Icons.Default.Timer to "No books currently being read"
            LibraryTab.READ -> Icons.Default.CheckCircle to "No finished books yet"
            LibraryTab.TO_READ -> Icons.Default.Bookmark to "No books in your reading list"
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TertiaryTextColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = SecondaryTextColor
        )
    }
}

package com.honari.app.presentation.screens.reading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.honari.app.domain.model.CurrentBook
import com.honari.app.domain.model.ReadingGoal
import com.honari.app.domain.model.RecentQuote
import com.honari.app.presentation.theme.BackgroundColor
import com.honari.app.presentation.theme.HeroBackground
import com.honari.app.presentation.theme.MoodContemplative
import com.honari.app.presentation.theme.MoodDreamy
import com.honari.app.presentation.theme.MoodNostalgic
import com.honari.app.presentation.theme.MoodRomantic
import com.honari.app.presentation.theme.PrimaryColor
import com.honari.app.presentation.theme.PrimaryTextColor
import com.honari.app.presentation.theme.SecondaryTextColor
import com.honari.app.presentation.theme.SurfaceLight
import com.honari.app.presentation.theme.TertiaryTextColor
import com.honari.app.presentation.theme.TrendingColor
import kotlinx.coroutines.delay

/**
 * Enhanced Reading screen with animations and improved design.
 */
@Composable
fun ReadingScreen(
    navController: NavController,
    viewModel: ReadingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Header
        item {
            ReadingHeader(
                onCalendarClick = { /* TODO: Show reading calendar */ }
            )
        }

        // Currently Reading Books with enhanced cards
        item {
            CurrentlyReadingSection(
                books = uiState.currentBooks,
                onContinueReading = { book -> /* TODO: Navigate to reader */ },
                onQuoteClick = { book -> /* TODO: Show quote capture */ }
            )
        }

        // Today's Goals with animations
        item {
            ReadingGoalsSection(goals = uiState.todayGoals)
        }

        // Recent Captures with beautiful cards
        item {
            RecentQuotesSection(quotes = uiState.recentQuotes)
        }

        // Reading Session CTA
        item {
            ReadingSessionCard(
                isSessionActive = uiState.isSessionActive,
                onSessionToggle = viewModel::toggleSession
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

/**
 * Reading header.
 */
@Composable
private fun ReadingHeader(onCalendarClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Reading",
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your literary journey",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryTextColor
            )
        }

        IconButton(
            onClick = onCalendarClick,
            modifier = Modifier
                .size(40.dp)
                .background(SurfaceLight, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Calendar",
                tint = PrimaryTextColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Currently reading section with enhanced book cards.
 */
@Composable
private fun CurrentlyReadingSection(
    books: List<CurrentBook>,
    onContinueReading: (CurrentBook) -> Unit,
    onQuoteClick: (CurrentBook) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Currently Reading",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            books.forEach { book ->
                CurrentBookCard(
                    book = book,
                    onContinueReading = { onContinueReading(book) },
                    onQuoteClick = { onQuoteClick(book) }
                )
            }
        }
    }
}

/**
 * Enhanced current book card.
 */
@Composable
private fun CurrentBookCard(
    book: CurrentBook,
    onContinueReading: () -> Unit,
    onQuoteClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Book Cover
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = book.imageUrl,
                        contentDescription = book.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Progress overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${book.progress}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Book Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
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

                    // Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Page ${book.currentPage} of ${book.totalPages}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryTextColor
                            )
                            Text(
                                text = "${book.pagesLeft} pages left",
                                style = MaterialTheme.typography.bodySmall,
                                color = MoodDreamy,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        val animatedProgress by animateFloatAsState(
                            targetValue = book.progress / 100f,
                            animationSpec = tween(1000),
                            label = "progress"
                        )

                        LinearProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MoodDreamy,
                            trackColor = SurfaceLight
                        )
                    }

                    // Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = SecondaryTextColor
                            )
                            Text(
                                text = "${book.readingTime}min today",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryTextColor
                            )
                        }
                        Text(
                            text = "~${book.estimatedTime} left",
                            style = MaterialTheme.typography.bodySmall,
                            color = MoodNostalgic,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Expandable Actions
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onContinueReading() },
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryColor
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Continue Reading",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onQuoteClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(SurfaceLight, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Capture Quote",
                            tint = PrimaryTextColor
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reading goals section with animated progress.
 */
@Composable
private fun ReadingGoalsSection(goals: List<ReadingGoal>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Today's Goals",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
        ) {
            items(goals) { goal ->
                GoalCard(goal = goal)
            }
        }
    }
}

/**
 * Goal card with circular progress.
 */
@Composable
private fun GoalCard(goal: ReadingGoal) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isVisible) (goal.current.toFloat() / goal.target) else 0f,
        animationSpec = tween(1000),
        label = "goal_progress"
    )

    Card(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HeroBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Circular progress background
                Canvas(
                    modifier = Modifier.size(60.dp)
                ) {
                    drawCircle(
                        color = SurfaceLight,
                        radius = size.minDimension / 2
                    )
                    drawArc(
                        color = TrendingColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Icon(
                    imageVector = goal.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = PrimaryTextColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = goal.type.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryTextColor
            )

            Text(
                text = "${goal.current} / ${goal.target}",
                style = MaterialTheme.typography.bodyLarge,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = goal.unit,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryTextColor
            )
        }
    }
}

/**
 * Recent quotes section.
 */
@Composable
private fun RecentQuotesSection(quotes: List<RecentQuote>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Recent Captures",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            quotes.forEach { quote ->
                QuoteCard(quote = quote)
            }
        }
    }
}

/**
 * Beautiful quote card.
 */
@Composable
private fun QuoteCard(quote: RecentQuote) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MoodRomantic
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "\"${quote.text}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryTextColor,
                    fontStyle = FontStyle.Italic,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = quote.bookTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryTextColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "p. ${quote.page}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TertiaryTextColor
                    )
                }

                Text(
                    text = quote.dateAdded,
                    style = MaterialTheme.typography.bodySmall,
                    color = TertiaryTextColor
                )
            }
        }
    }
}

/**
 * Reading session card.
 */
@Composable
private fun ReadingSessionCard(
    isSessionActive: Boolean,
    onSessionToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onSessionToggle() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSessionActive) MoodContemplative else PrimaryColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isSessionActive) "Reading session in progress" else "Start a reading session",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isSessionActive) "Stay focused on your book" else "Track your focus time",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Surface(
                modifier = Modifier.padding(top = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isSessionActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = if (isSessionActive) "Pause Session" else "Begin Session",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

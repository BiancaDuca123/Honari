package com.honari.app.presentation.screens.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.honari.app.R
import com.honari.app.domain.model.GenrePreference
import com.honari.app.domain.model.Milestone
import com.honari.app.domain.model.ProfileActivity
import com.honari.app.domain.model.YearlyStat
import com.honari.app.presentation.navigation.Screen
import com.honari.app.presentation.screens.auth.AuthViewModel
import com.honari.app.presentation.theme.BackgroundColor
import com.honari.app.presentation.theme.ErrorColor
import com.honari.app.presentation.theme.HeroBackground
import com.honari.app.presentation.theme.MoodDreamy
import com.honari.app.presentation.theme.MoodRomantic
import com.honari.app.presentation.theme.PrimaryColor
import com.honari.app.presentation.theme.PrimaryTextColor
import com.honari.app.presentation.theme.RatingStarColor
import com.honari.app.presentation.theme.SecondaryTextColor
import com.honari.app.presentation.theme.SurfaceLight
import com.honari.app.presentation.theme.TertiaryTextColor
import com.honari.app.presentation.theme.TrendingColor
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    val authUiState by authViewModel.uiState.collectAsState()
    val profileUiState by profileViewModel.uiState.collectAsState()
    val user = authUiState.currentUser

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Header with gradient background
        item {
            ProfileHeaderSection(
                user = user,
                onShareClick = { /* TODO: Share profile */ },
                onSettingsClick = { /* TODO: Navigate to settings */ }
            )
        }

        // Animated Stats
        item {
            AnimatedStatsSection(stats = profileUiState.yearlyStats)
        }

        // Reading Milestones
        item {
            MilestonesSection(milestones = profileUiState.milestones)
        }

        // Reading DNA with animated chart
        item {
            ReadingDNASection(genres = profileUiState.favoriteGenres)
        }

        // Recent Activity
        item {
            RecentActivitySection(activities = profileUiState.recentActivities)
        }

        // Share Profile CTA
        item {
            ShareProfileCard(
                onShareClick = { /* TODO: Share profile */ }
            )
        }

        // Logout Button
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ErrorColor // make sure this matches your theme or define it
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(true).copy(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(ErrorColor, ErrorColor)
                        )
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.logout),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

/**
 * Profile header with gradient background.
 */
@Composable
private fun ProfileHeaderSection(
    user: com.honari.app.domain.model.User?,
    onShareClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryColor.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor.copy(alpha = 0.2f))
                ) {
                    if (user?.profileImageUrl != null) {
                        AsyncImage(
                            model = user.profileImageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = user?.displayName?.firstOrNull()?.toString() ?: "?",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.headlineMedium,
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // User Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = user?.displayName ?: "Reader",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryTextColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@${user?.email?.substringBefore('@') ?: "reader"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryTextColor
                    )
                    Text(
                        text = "Finding solace in stories, one page at a time ✨",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryTextColor,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceLight, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = PrimaryTextColor
                    )
                }
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceLight, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = PrimaryTextColor
                    )
                }
            }
        }
    }
}

/**
 * Animated stats section.
 */
@Composable
private fun AnimatedStatsSection(stats: List<YearlyStat>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "2024 Reading Journey",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
        ) {
            items(stats) { stat ->
                AnimatedStatCard(stat = stat)
            }
        }
    }
}

/**
 * Animated stat card.
 */
@Composable
private fun AnimatedStatCard(stat: YearlyStat) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val animatedValue by animateFloatAsState(
        targetValue = if (isVisible) stat.value.toFloatOrNull() ?: 0f else 0f,
        animationSpec = tween(1000),
        label = "stat_animation"
    )

    Card(
        modifier = Modifier
            .width(100.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(stat.color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = stat.color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (stat.label == "Avg Rating") {
                    String.format("%.1f", animatedValue)
                } else {
                    animatedValue.toInt().toString()
                },
                style = MaterialTheme.typography.headlineSmall,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stat.label,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryTextColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Milestones section with progress indicators.
 */
@Composable
private fun MilestonesSection(milestones: List<Milestone>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Milestones",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        val rows = milestones.chunked(2)
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { milestone ->
                        MilestoneCard(
                            milestone = milestone,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Milestone card with achievement indicator.
 */
@Composable
private fun MilestoneCard(
    milestone: Milestone,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { /* TODO: Show milestone details */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.achieved) Color.White else HeroBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (milestone.achieved) 4.dp else 0.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = milestone.icon,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (milestone.achieved) PrimaryTextColor else TertiaryTextColor,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = milestone.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (milestone.achieved) SecondaryTextColor else TertiaryTextColor,
                    textAlign = TextAlign.Center
                )
            }

            if (milestone.achieved) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(12.dp)
                        .background(TrendingColor, CircleShape)
                )
            }
        }
    }
}

/**
 * Reading DNA section with animated chart.
 */
@Composable
private fun ReadingDNASection(genres: List<GenrePreference>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Reading DNA",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                genres.forEach { genre ->
                    GenreProgressBar(genre = genre)
                }
            }
        }
    }
}

/**
 * Animated genre progress bar.
 */
@Composable
private fun GenreProgressBar(genre: GenrePreference) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isVisible) genre.percentage / 100f else 0f,
        animationSpec = tween(1000),
        label = "genre_progress"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${genre.percentage}%",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryTextColor,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(SurfaceLight, RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(genre.color, RoundedCornerShape(3.dp))
            )
        }
    }
}

/**
 * Recent activity section.
 */
@Composable
private fun RecentActivitySection(activities: List<ProfileActivity>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            activities.forEach { activity ->
                ActivityCard(activity = activity)
            }
        }
    }
}

/**
 * Activity card component.
 */
@Composable
private fun ActivityCard(activity: ProfileActivity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HeroBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = when (activity.type) {
                    "finished" -> Icons.Default.CheckCircle
                    "joined" -> Icons.Default.Groups
                    "quote" -> Icons.Default.FormatQuote
                    "started" -> Icons.AutoMirrored.Filled.MenuBook
                    else -> Icons.Default.BookmarkBorder
                },
                contentDescription = null,
                tint = when (activity.type) {
                    "finished" -> TrendingColor
                    "joined" -> MoodDreamy
                    "quote" -> MoodRomantic
                    "started" -> PrimaryColor
                    else -> SecondaryTextColor
                }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryTextColor
                )
                Text(
                    text = activity.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor
                )
            }

            activity.rating?.let { rating ->
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
    }
}

/**
 * Share profile card.
 */
@Composable
private fun ShareProfileCard(onShareClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onShareClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Share your reading journey",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Let others discover your literary taste and connect over shared stories",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Surface(
                modifier = Modifier.padding(top = 12.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = "Share Profile",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
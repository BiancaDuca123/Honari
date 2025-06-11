package com.honari.app.presentation.screens.circles

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.honari.app.domain.model.ActivityType
import com.honari.app.domain.model.CircleActivity
import com.honari.app.domain.model.MyCircle
import com.honari.app.domain.model.SuggestedCircle
import com.honari.app.presentation.theme.BackgroundColor
import com.honari.app.presentation.theme.HeroBackground
import com.honari.app.presentation.theme.MoodDreamy
import com.honari.app.presentation.theme.PrimaryTextColor
import com.honari.app.presentation.theme.RatingStarColor
import com.honari.app.presentation.theme.SecondaryTextColor
import com.honari.app.presentation.theme.SuccessColor
import com.honari.app.presentation.theme.SurfaceLight
import com.honari.app.presentation.theme.TertiaryTextColor
import com.honari.app.presentation.theme.TrendingColor

/**
 * Circles screen composable.
 * Displays reading circles/groups and social features.
 */
@Composable
fun CirclesScreen(
    navController: NavController, viewModel: CirclesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // Header
        item {
            CirclesHeader(
                onSearchClick = { /* TODO: Navigate to search */ },
                onCreateClick = { /* TODO: Navigate to create circle */ })
        }

        // My Circles Section
        item {
            Text(
                text = "My Circles",
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryTextColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        uiState.myCircles.forEach { circle ->
            item {
                MyCircleCard(
                    circle = circle,
                    onClick = { /* TODO: Navigate to circle detail */ },
                    onMessageClick = { /* TODO: Open messages */ })
            }
        }

        // Suggested Circles Section
        item {
            Text(
                text = "Discover Circles",
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryTextColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 32.dp, bottom = 16.dp)
            )
        }

        uiState.suggestedCircles.forEach { circle ->
            item {
                SuggestedCircleCard(
                    circle = circle, onJoinClick = { viewModel.joinCircle(circle.id) })
            }
        }

        // Recent Activity Section
        item {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryTextColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 32.dp, bottom = 16.dp)
            )
        }

        uiState.recentActivities.forEach { activity ->
            item {
                ActivityCard(activity = activity)
            }
        }

        // Create Circle CTA
        item {
            CreateCircleCTA(
                onCreateClick = { /* TODO: Navigate to create circle */ })
        }
    }
}

/**
 * Circles header component.
 */
@Composable
private fun CirclesHeader(
    onSearchClick: () -> Unit, onCreateClick: () -> Unit
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
                text = "Circles",
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your literary communities",
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
                onClick = onCreateClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryTextColor, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Circle",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * My circle card component.
 */
@Composable
private fun MyCircleCard(
    circle: MyCircle, onClick: () -> Unit, onMessageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.Top
        ) {
            // Circle Image
            AsyncImage(
                model = circle.imageUrl,
                contentDescription = circle.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Circle Info
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Name with active indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = circle.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = PrimaryTextColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (circle.isActive) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(SuccessColor, CircleShape)
                        )
                    }
                }

                // Description
                Text(
                    text = circle.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryTextColor,
                    lineHeight = 20.sp
                )

                // Stats
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = SecondaryTextColor
                        )
                        Text(
                            text = "${circle.memberCount} members",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTextColor
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = SecondaryTextColor
                        )
                        Text(
                            text = "${circle.bookCount} books",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTextColor
                        )
                    }
                }

                // Activity info
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = buildAnnotatedString {
                            append("Currently reading: ")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Medium, color = PrimaryTextColor
                                )
                            ) {
                                append(circle.currentBook)
                            }
                        }, style = MaterialTheme.typography.bodySmall, color = SecondaryTextColor
                    )
                    Text(
                        text = "Last activity ${circle.lastActivity}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TertiaryTextColor
                    )
                }
            }

            // Message button
            IconButton(
                onClick = onMessageClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceLight, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Message",
                    tint = PrimaryTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Suggested circle card component.
 */
@Composable
private fun SuggestedCircleCard(
    circle: SuggestedCircle, onJoinClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.Top
        ) {
            // Circle Image
            AsyncImage(
                model = circle.imageUrl,
                contentDescription = circle.name,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Circle Info
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Name with match badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = circle.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = PrimaryTextColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Surface(
                        color = Color(0xFFE8F5E8), shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${circle.matchPercent}% match",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessColor,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Description
                Text(
                    text = circle.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor,
                    lineHeight = 18.sp
                )

                // Stats
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = SecondaryTextColor
                        )
                        Text(
                            text = circle.memberCount.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTextColor
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = SecondaryTextColor
                        )
                        Text(
                            text = "${circle.bookCount} books",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTextColor
                        )
                    }
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
                            text = "${circle.commonBooks} in common",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTextColor
                        )
                    }
                }
            }

            // Join button
            Button(
                onClick = onJoinClick,
                modifier = Modifier
                    .height(32.dp)
                    .align(Alignment.Top),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTextColor),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = "Join", style = MaterialTheme.typography.labelLarge, color = Color.White
                )
            }
        }
    }
}

/**
 * Activity card component.
 */
@Composable
private fun ActivityCard(activity: CircleActivity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HeroBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.Top
        ) {
            // User Avatar
            AsyncImage(
                model = activity.userAvatar,
                contentDescription = activity.userName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Activity Info
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Activity text
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.SemiBold, color = PrimaryTextColor
                            )
                        ) {
                            append(activity.userName)
                        }
                        append(" ${activity.action} ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Medium, color = MoodDreamy
                            )
                        ) {
                            append(activity.bookTitle)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryTextColor,
                    lineHeight = 20.sp
                )

                // Meta info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = activity.circleName,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryTextColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = activity.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = TertiaryTextColor
                    )
                }
            }

            // Activity icon
            Box(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                when (activity.type) {
                    ActivityType.DISCUSSION -> Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MoodDreamy
                    )

                    ActivityType.RECOMMENDATION -> Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = RatingStarColor
                    )

                    ActivityType.QUOTE -> Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TrendingColor
                    )
                }
            }
        }
    }
}

/**
 * Create circle CTA component.
 */
@Composable
private fun CreateCircleCTA(onCreateClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 32.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HeroBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Start your own circle",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryTextColor,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Gather fellow readers around the books you love most",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryTextColor,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onCreateClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTextColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Circle",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
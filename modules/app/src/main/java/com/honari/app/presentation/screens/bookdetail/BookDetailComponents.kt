package com.honari.app.presentation.screens.bookdetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.honari.app.domain.model.Review
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.StarGold
import com.honari.app.presentation.theme.TextPrimary
import com.honari.app.presentation.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val COVER_WIDTH = 150.dp
private val COVER_HEIGHT = 220.dp
private const val REVIEW_DATE_PATTERN = "MMM d, yyyy"
private const val MAX_STARS = 5
private const val RATING_ICON_SIZE = 16

@Composable
internal fun BookCover(imageUrl: String, title: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.size(width = COVER_WIDTH, height = COVER_HEIGHT),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        if (imageUrl.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = BrownHeadline)
            }
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
internal fun BookRatingRow(rating: Float, ratingsCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..MAX_STARS) {
            Icon(
                imageVector = if (i <= rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = StarGold,
                modifier = Modifier.size(RATING_ICON_SIZE.dp),
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (rating > 0f) {
                String.format(Locale.US, "%.1f", rating) +
                    if (ratingsCount > 0) " ($ratingsCount)" else ""
            } else {
                "No rating yet"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun ReviewsHeader(reviewCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Reviews",
            style = MaterialTheme.typography.headlineSmall,
            color = BrownHeadline,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "$reviewCount review${if (reviewCount == 1) "" else "s"}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
}

@Composable
internal fun ReviewCard(
    review: Review,
    isOwner: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ReviewAvatar(photoUrl = review.photoUrl)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.displayName.ifEmpty { "Honari Reader" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Text(
                        text = formatReviewDate(review.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                if (isOwner) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit review",
                            tint = PrimaryTeal,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete review",
                            tint = ErrorRed,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            ReviewStarRow(rating = review.rating)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = review.text.ifEmpty { "Shared a thoughtful review." },
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ReviewStarRow(rating: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..MAX_STARS) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = StarGold,
                modifier = Modifier.size(14.dp),
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format(Locale.US, "%.1f", rating),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReviewAvatar(photoUrl: String?) {
    if (photoUrl.isNullOrEmpty()) {
        Box(
            modifier = Modifier.size(38.dp).clip(CircleShape).background(PrimaryTeal.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = PrimaryTeal)
        }
    } else {
        AsyncImage(
            model = photoUrl,
            contentDescription = null,
            modifier = Modifier.size(38.dp).clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
internal fun EmptyReviewsState() {
    Text(
        text = "No reviews yet. Be the first to share your thoughts!",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    )
}

internal fun formatLanguage(language: String): String =
    language.replaceFirstChar { c ->
        if (c.isLowerCase()) c.titlecase(Locale.US) else c.toString()
    }.ifEmpty { EMPTY_METRIC }

internal fun formatPages(pageCount: Int): String =
    pageCount.takeIf { it > 0 }?.toString() ?: EMPTY_METRIC

private fun formatReviewDate(createdAt: Long): String {
    if (createdAt <= 0L) return ""
    return SimpleDateFormat(REVIEW_DATE_PATTERN, Locale.US).format(Date(createdAt))
}

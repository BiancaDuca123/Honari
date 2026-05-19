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
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.honari.app.domain.model.Review
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.TextPrimary
import com.honari.app.presentation.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val COVER_WIDTH = 150.dp
private val COVER_HEIGHT = 220.dp
private const val REVIEW_DATE_PATTERN = "MMM d, yyyy"

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
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = BrownHeadline,
                )
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
internal fun ReviewsHeader(reviewCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Reviews:",
            style = MaterialTheme.typography.headlineSmall,
            color = BrownHeadline,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "($reviewCount People Reviewed)",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
}

@Composable
internal fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            ReviewAvatar(photoUrl = review.photoUrl)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = review.displayName.ifEmpty { "Honari Reader" },
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                    )
                    Text(
                        text = formatReviewDate(review.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = review.text.ifEmpty { "Shared a thoughtful review." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ReviewAvatar(photoUrl: String?) {
    if (photoUrl.isNullOrEmpty()) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PrimaryTeal.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = PrimaryTeal,
            )
        }
    } else {
        AsyncImage(
            model = photoUrl,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
internal fun EmptyReviewsState() {
    Text(
        text = "No reviews yet. Be the first to add this book to your reading journey.",
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    )
}

internal fun formatLanguage(language: String): String =
    language.replaceFirstChar { character ->
        if (character.isLowerCase()) {
            character.titlecase(Locale.US)
        } else {
            character.toString()
        }
    }.ifEmpty { EMPTY_METRIC }

internal fun formatPages(pageCount: Int): String =
    pageCount.takeIf { it > 0 }?.toString() ?: EMPTY_METRIC

private fun formatReviewDate(createdAt: Long): String {
    if (createdAt <= 0L) {
        return ""
    }
    return SimpleDateFormat(REVIEW_DATE_PATTERN, Locale.US).format(Date(createdAt))
}

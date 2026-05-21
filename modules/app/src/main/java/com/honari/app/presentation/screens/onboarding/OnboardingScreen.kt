package com.honari.app.presentation.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.honari.app.presentation.theme.PrimaryTealDark
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.PrimaryTeal

private val OnboardingIconSize = 120.dp
private val PillShape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
private const val LAST_SLIDE_INDEX = 2

private data class OnboardingSlide(
    val title: String,
    val description: String,
    val icon: ImageVector,
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val slides = remember {
        listOf(
            OnboardingSlide(
                icon = Icons.Default.AutoStories,
                title = "Find Your Next Great Read!",
                description = "Browse thousands of books, from timeless classics to the " +
                    "latest bestsellers.",
            ),
            OnboardingSlide(
                icon = Icons.Default.LibraryBooks,
                title = "Curate Your Library!",
                description = "Create and manage your own collections. Save books you love " +
                    "and keep track of what you want to read next.",
            ),
            OnboardingSlide(
                icon = Icons.Default.Group,
                title = "Join a Community of Book Lovers!",
                description = "Follow friends, share reviews, and discuss your favorite books " +
                    "with like-minded readers.",
            ),
        )
    }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    val slide = slides[currentIndex]
    val progress = (currentIndex + 1).toFloat() / slides.size.toFloat()
    val buttonText = if (currentIndex == LAST_SLIDE_INDEX) "Get Started" else "Next"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(PillShape),
            color = PrimaryTeal,
            trackColor = CardWhite,
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = slide.icon,
            contentDescription = null,
            tint = PrimaryTeal,
            modifier = Modifier.size(OnboardingIconSize),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = slide.title,
            style = MaterialTheme.typography.displayMedium,
            color = PrimaryTealDark,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = slide.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (currentIndex == LAST_SLIDE_INDEX) {
                    onFinished()
                } else {
                    currentIndex += 1
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
        ) {
            Text(text = buttonText)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onFinished) {
            Text(
                text = "Skip",
                color = PrimaryTealDark,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

package com.honari.app.presentation.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.honari.app.domain.model.Book
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.TextPrimary
import com.honari.app.presentation.theme.TextSecondary

@Composable
internal fun FolderList(folders: List<LibraryFolder>, onSelectFilter: (LibraryFilter) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(folders, key = { it.filter.name }) { folder ->
            FolderRow(folder = folder, onClick = { onSelectFilter(folder.filter) })
        }
    }
}

@Composable
private fun FolderRow(folder: LibraryFolder, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThumbnailStack(books = folder.books)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildFolderSubtitle(folder.books),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary,
            )
        }
    }
}

@Composable
private fun ThumbnailStack(books: List<Book>) {
    Box(
        modifier = Modifier
            .width(56.dp)
            .height(52.dp),
    ) {
        val previews = books.take(2)
        if (previews.isEmpty()) {
            ThumbnailPlaceholder(modifier = Modifier.align(Alignment.CenterStart))
            return
        }
        previews.forEachIndexed { index, book ->
            ThumbnailImage(
                imageUrl = book.imageUrl,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (index * 18).dp),
            )
        }
    }
}

@Composable
internal fun ThumbnailImage(imageUrl: String, modifier: Modifier = Modifier) {
    if (imageUrl.isEmpty()) {
        ThumbnailPlaceholder(modifier = modifier)
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = modifier
                .size(width = 32.dp, height = 48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun ThumbnailPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 32.dp, height = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryTeal.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            tint = PrimaryTeal,
            modifier = Modifier.size(18.dp),
        )
    }
}

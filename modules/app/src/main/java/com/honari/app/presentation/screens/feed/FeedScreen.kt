package com.honari.app.presentation.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.honari.app.domain.model.Book
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal

private const val SKELETON_COUNT = 6
private const val NEW_RELEASES_COUNT = 5
private const val SKELETON_ALPHA = 0.35f
private const val SECTION_FEATURED = "Featured"
private const val SECTION_NEW_RELEASES = "New Releases"
private const val SECTION_TOP_PICKS = "Top Picks for You"

@Composable
fun FeedScreen(
    onBookClick: (String) -> Unit,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSearch by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ExploreTopBar(onToggleSearch = { showSearch = !showSearch })
            if (showSearch || uiState.searchQuery.isNotEmpty()) {
                SearchField(
                    query = uiState.searchQuery,
                    onQueryChanged = viewModel::onSearchQueryChanged,
                    onClear = {
                        viewModel.clearSearch()
                        showSearch = false
                    },
                )
            }
            when {
                uiState.isLoading && uiState.books.isEmpty() -> LoadingFeed()
                uiState.searchQuery.isNotEmpty() -> SearchResultsContent(
                    books = uiState.searchResults,
                    isSearching = uiState.isSearching,
                    query = uiState.searchQuery,
                    onBookClick = onBookClick,
                )
                else -> ExploreContent(
                    books = uiState.books,
                    topPicksBooks = uiState.topPicksBooks,
                    selectedGenre = uiState.selectedGenre,
                    isRefreshing = uiState.isLoading,
                    onRefresh = viewModel::refreshFeed,
                    onGenreSelected = viewModel::onGenreSelected,
                    onBookClick = onBookClick,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        ) { data ->
            Snackbar(containerColor = ErrorRed, contentColor = CardWhite) {
                Text(text = data.visuals.message)
            }
        }
    }
}

@Composable
private fun ExploreTopBar(onToggleSearch: () -> Unit) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    top = statusBarPadding + 16.dp,
                    end = 12.dp,
                    bottom = 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Explore",
                style = MaterialTheme.typography.headlineLarge,
                color = BrownHeadline,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onToggleSearch) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = BrownHeadline.copy(alpha = 0.12f),
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        placeholder = {
            Text(
                text = "Search books, authors...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = PrimaryTeal,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = PrimaryTeal,
        ),
    )
}

@Composable
private fun ExploreContent(
    books: List<Book>,
    topPicksBooks: List<Book>,
    selectedGenre: String?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onGenreSelected: (String) -> Unit,
    onBookClick: (String) -> Unit,
) {
    val displayed = filterByGenre(books, selectedGenre)
    val featured = displayed.firstOrNull()
    val newReleases = displayed.drop(1).take(NEW_RELEASES_COUNT)
    val picks = if (topPicksBooks.isNotEmpty()) topPicksBooks else displayed.drop(1 + NEW_RELEASES_COUNT)

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
        if (books.isEmpty()) {
            EmptyState(message = "No books available yet. Pull to refresh.")
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                item {
                    GenreChipRow(
                        selectedGenre = selectedGenre,
                        onGenreSelected = onGenreSelected,
                    )
                }
                featured?.let { book ->
                    item { SectionTitle(title = SECTION_FEATURED, topPadding = 0.dp) }
                    item { FeaturedBookCard(book = book, onClick = { onBookClick(book.id) }) }
                }
                item { SectionTitle(title = SECTION_NEW_RELEASES, topPadding = 16.dp) }
                item { HorizontalBooksRow(books = newReleases, onBookClick = onBookClick) }
                item { SectionTitle(title = SECTION_TOP_PICKS, topPadding = 16.dp) }
                items(picks.chunked(2), key = { it.first().id }) { pair ->
                    BookCardRow(pair = pair, onBookClick = onBookClick)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, topPadding: Dp) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = BrownHeadline,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = topPadding, bottom = 4.dp),
    )
}

@Composable
private fun SearchResultsContent(
    books: List<Book>,
    isSearching: Boolean,
    query: String,
    onBookClick: (String) -> Unit,
) {
    when {
        isSearching -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryTeal)
            }
        }

        books.isEmpty() -> EmptyState(message = "No books found for \"$query\".")

        else -> {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(books, key = { it.id }) { book ->
                    SearchResultItem(book = book, onClick = { onBookClick(book.id) })
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(width = 64.dp, height = 96.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = book.authors.joinToString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (book.averageRating > 0f) {
                    RatingRow(rating = book.averageRating)
                }
            }
        }
    }
}

@Composable
private fun LoadingFeed() {
    val skeletonColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = SKELETON_ALPHA)
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
        item { SectionTitle(title = SECTION_FEATURED, topPadding = 8.dp) }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FEATURED_HEIGHT.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(skeletonColor),
            )
        }
        item { SectionTitle(title = SECTION_NEW_RELEASES, topPadding = 16.dp) }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(NEW_RELEASES_COUNT) {
                    Box(
                        modifier = Modifier
                            .width(COMPACT_CARD_WIDTH.dp)
                            .height(COMPACT_CARD_HEIGHT.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(skeletonColor),
                    )
                }
            }
        }
        item { SectionTitle(title = SECTION_TOP_PICKS, topPadding = 16.dp) }
        items(SKELETON_COUNT / 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(BOOK_CARD_IMAGE_HEIGHT.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(skeletonColor),
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(24.dp),
        )
    }
}

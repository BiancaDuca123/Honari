package com.honari.app.presentation.screens.library

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.honari.app.R
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.presentation.theme.HonariTheme
import com.honari.app.presentation.theme.RatingStarColor
import com.honari.app.presentation.theme.SuccessColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(onScanClick: () -> Unit, viewModel: LibraryViewModel) {
    val state by viewModel.uiState.collectAsState()
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.pendingDeleteId) {
        val pendingId = state.pendingDeleteId ?: return@LaunchedEffect
        val pendingBook = state.books.find { it.id == pendingId } ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = "${pendingBook.title} removed",
            actionLabel = "Undo",
            duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) viewModel.undoDeletion()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onScanClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Scan book",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = stringResource(R.string.my_library),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 12.dp
                )
            )
            LibraryFilterRow(selected = state.selectedFilter, onSelect = viewModel::setFilter)
            Spacer(Modifier.height(8.dp))
            when {
                state.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                state.error != null -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                }
                state.filtered.isEmpty() -> LibraryEmptyState()
                else -> LibraryBookList(
                    books = state.filtered,
                    onBookClick = { book ->
                        selectedBook = book
                        scope.launch { sheetState.show() }
                    },
                    onBookDelete = { book -> viewModel.markForDeletion(book.id) }
                )
            }
        }
    }

    if (selectedBook != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedBook = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            BookActionSheet(
                book = selectedBook!!,
                onProgressSave = { progress ->
                    viewModel.updateProgress(selectedBook!!.id, progress)
                    scope.launch {
                        sheetState.hide()
                        selectedBook = null
                    }
                },
                onStatusSelected = { status ->
                    viewModel.updateBookStatus(selectedBook!!.id, status)
                    scope.launch {
                        sheetState.hide()
                        selectedBook = null
                    }
                }
            )
        }
    }
}

@Composable
private fun LibraryFilterRow(selected: ReadingStatus?, onSelect: (ReadingStatus?) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(selected = selected == null, onClick = {
            onSelect(null)
        }, label = { Text(stringResource(R.string.filter_all)) })
        FilterChip(selected = selected == ReadingStatus.READING, onClick = {
            onSelect(ReadingStatus.READING)
        }, label = { Text(stringResource(R.string.filter_reading)) })
        FilterChip(selected = selected == ReadingStatus.FINISHED, onClick = {
            onSelect(ReadingStatus.FINISHED)
        }, label = { Text(stringResource(R.string.filter_read)) })
        FilterChip(selected = selected == ReadingStatus.WANT_TO_READ, onClick = {
            onSelect(ReadingStatus.WANT_TO_READ)
        }, label = { Text(stringResource(R.string.filter_want)) })
    }
}

@Composable
private fun LibraryEmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(R.string.empty_library_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.empty_library_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun LibraryBookList(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    onBookDelete: (Book) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
        items(books, key = { it.id }) { book ->
            SwipeableBookItem(book = book, onClick = {
                onBookClick(book)
            }, onDelete = { onBookDelete(book) })
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableBookItem(book: Book, onClick: () -> Unit, onDelete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.dismissDirection ==
                    SwipeToDismissBoxValue.EndToStart
                ) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color.Transparent
                },
                label = "swipe_bg"
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                    color,
                    RoundedCornerShape(8.dp)
                ).padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                }
            }
        }
    ) {
        LibraryBookItem(book = book, onClick = onClick)
    }
}

@Composable
private fun LibraryBookItem(book: Book, onClick: () -> Unit) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(
                onClick = onClick
            ).padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp, 80.dp).clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    book.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                StatusChip(status = book.status)
                if (book.status == ReadingStatus.READING.name && book.progress > 0) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { book.progress / 100f },
                            modifier = Modifier.weight(
                                1f
                            ).height(5.dp).clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${book.progress}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

@Composable
private fun StatusChip(status: String) {
    val (label, containerColor) = when (status) {
        ReadingStatus.READING.name ->
            "Reading" to
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ReadingStatus.FINISHED.name -> "Read" to SuccessColor.copy(alpha = 0.18f)
        else ->
            "Want to Read" to
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
    }
    val textColor = when (status) {
        ReadingStatus.READING.name -> MaterialTheme.colorScheme.primary
        ReadingStatus.FINISHED.name -> SuccessColor
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(color = containerColor, shape = RoundedCornerShape(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/** Combined sheet: progress slider (if READING) + status picker. */
@Composable
private fun BookActionSheet(
    book: Book,
    onProgressSave: (Int) -> Unit,
    onStatusSelected: (ReadingStatus) -> Unit
) {
    var sliderProgress by remember { mutableFloatStateOf(book.progress.toFloat()) }

    Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = 16.dp)) {
        Text(
            text = book.title.ifBlank { "Book actions" },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        if (book.status == ReadingStatus.READING.name) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Reading progress",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "${sliderProgress.toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Slider(
                    value = sliderProgress,
                    onValueChange = { sliderProgress = it },
                    valueRange = 0f..100f,
                    steps = 19,
                    modifier = Modifier.fillMaxWidth()
                )
                if (book.pageCount > 0) {
                    val pagesRead = (sliderProgress / 100f * book.pageCount).toInt()
                    Text(
                        "$pagesRead / ${book.pageCount} pages",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .clickable { onProgressSave(sliderProgress.toInt()) },
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Save progress",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Text(
                "Change status",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        val statusOptions = listOf(
            Triple(
                ReadingStatus.READING,
                R.string.status_reading,
                Icons.AutoMirrored.Filled.MenuBook
            ),
            Triple(ReadingStatus.FINISHED, R.string.status_finished, Icons.Default.CheckCircle),
            Triple(ReadingStatus.WANT_TO_READ, R.string.status_want, Icons.Default.Bookmark)
        )
        statusOptions.forEach { (status, labelRes, icon) ->
            val isSelected = book.status == status.name
            val tint = when (status) {
                ReadingStatus.READING -> MaterialTheme.colorScheme.primary
                ReadingStatus.FINISHED -> SuccessColor
                ReadingStatus.WANT_TO_READ -> RatingStarColor
            }
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(labelRes),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) tint else MaterialTheme.colorScheme.onBackground
                    )
                },
                leadingContent = { Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp)) },
                trailingContent = {
                    if (isSelected) {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            tint = tint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.clickable { onStatusSelected(status) }
            )
            if (status != ReadingStatus.WANT_TO_READ) {
                HorizontalDivider(
                    Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewLibraryBooks = listOf(
    Book(
        id = "1",
        title = "The Great Gatsby",
        author = "F. Scott Fitzgerald",
        status = ReadingStatus.READING.name,
        progress = 42,
        pageCount = 180
    ),
    Book(
        id = "2",
        title = "To Kill a Mockingbird",
        author = "Harper Lee",
        status = ReadingStatus.FINISHED.name
    ),
    Book(
        id = "3",
        title = "1984",
        author = "George Orwell",
        status = ReadingStatus.WANT_TO_READ.name
    )
)

@Preview(name = "Library – Light", showBackground = true, showSystemUi = true)
@Composable
private fun LibraryScreenLightPreview() {
    HonariTheme(darkTheme = false) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Text(
                "My Library",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
            )
            LibraryFilterRow(selected = null, onSelect = {})
            Spacer(Modifier.height(8.dp))
            LibraryBookList(books = previewLibraryBooks, onBookClick = {}, onBookDelete = {})
        }
    }
}

@Preview(
    name = "Library – Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun LibraryScreenDarkPreview() {
    HonariTheme(darkTheme = true) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Text(
                "My Library",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
            )
            LibraryFilterRow(selected = ReadingStatus.READING, onSelect = {})
            Spacer(Modifier.height(8.dp))
            LibraryBookList(books = previewLibraryBooks, onBookClick = {}, onBookDelete = {})
        }
    }
}

@Preview(name = "Book Action Sheet – Progress", showBackground = true)
@Composable
private fun BookActionSheetPreview() {
    HonariTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            BookActionSheet(book = previewLibraryBooks[0], onProgressSave = {
            }, onStatusSelected = {})
        }
    }
}

@Preview(name = "Library – Empty State", showBackground = true)
@Composable
private fun LibraryEmptyPreview() {
    HonariTheme(darkTheme = false) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            LibraryEmptyState()
        }
    }
}

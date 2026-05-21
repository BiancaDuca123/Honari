package com.honari.app.presentation.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.honari.app.presentation.theme.AccentCoral
import com.honari.app.presentation.theme.AccentIndigo
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.PrimaryTealDark

private val tabs = listOf(LibraryFilter.ALL_BOOKS, LibraryFilter.CONTINUE_READING, LibraryFilter.WISH_LIST)

private val LibraryFilter.tabLabel: String
    get() = when (this) {
        LibraryFilter.ALL_BOOKS -> "All"
        LibraryFilter.CONTINUE_READING -> "Finished"
        LibraryFilter.WISH_LIST -> "Want to Read"
    }

private fun LibraryFilter.tabColor(selected: Boolean): Color = when (this) {
    LibraryFilter.ALL_BOOKS -> if (selected) PrimaryTeal else Color.Transparent
    LibraryFilter.CONTINUE_READING -> if (selected) PrimaryTealDark else Color.Transparent
    LibraryFilter.WISH_LIST -> if (selected) AccentCoral else Color.Transparent
}

@Composable
fun LibraryScreen(
    onBookClick: (String) -> Unit = {},
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedTabIndex = tabs.indexOf(uiState.selectedFilter).coerceAtLeast(0)

    LaunchedEffect(uiState.selectedFilter) {
        if (uiState.selectedFilter == null) viewModel.selectFilter(LibraryFilter.ALL_BOOKS)
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LibraryHeader(
                bookCount = uiState.allBooks.size,
                topPadding = statusBarPadding.value.toInt(),
            )
            LibraryTabBar(
                tabs = tabs,
                selectedIndex = selectedTabIndex,
                onTabSelected = { viewModel.selectFilter(tabs[it]) },
            )
            LibraryContent(
                modifier = Modifier.fillMaxWidth().weight(1f),
                uiState = uiState,
                onRemoveBook = viewModel::removeBook,
                onBookClick = onBookClick,
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ) { data ->
            Snackbar(containerColor = ErrorRed, contentColor = CardWhite) {
                Text(text = data.visuals.message)
            }
        }
    }
}

@Composable
private fun LibraryTabBar(
    tabs: List<LibraryFilter>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tabs.forEachIndexed { index, filter ->
            val selected = selectedIndex == index
            val bgColor = filter.tabColor(selected)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selected) bgColor else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = filter.tabLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) CardWhite else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

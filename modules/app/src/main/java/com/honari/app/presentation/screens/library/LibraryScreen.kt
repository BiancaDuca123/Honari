package com.honari.app.presentation.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.PrimaryTealDark

private data class CollectionAction(val label: String, val filter: LibraryFilter)

private val collectionActions = listOf(
    CollectionAction("Continue Reading", LibraryFilter.CONTINUE_READING),
    CollectionAction("My Wish List", LibraryFilter.WISH_LIST),
    CollectionAction("All Books", LibraryFilter.ALL_BOOKS),
)

@Composable
fun LibraryScreen(viewModel: LibraryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val folders = remember(uiState.allBooks) { buildFolders(uiState.allBooks) }
    val sectionTitle = uiState.selectedFilter?.title ?: "My Folders"
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Text(
            text = "My Collections",
            style = MaterialTheme.typography.headlineLarge,
            color = BrownHeadline,
            modifier = Modifier.padding(
                start = 20.dp,
                top = statusBarPadding + 20.dp,
                end = 20.dp,
                bottom = 20.dp,
            ),
        )
        ActionButtonsRow(
            selectedFilter = uiState.selectedFilter,
            onSelectFilter = viewModel::selectFilter,
        )
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(
            title = sectionTitle,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        LibraryContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            uiState = uiState,
            folders = folders,
            onSelectFilter = viewModel::selectFilter,
        )
    }
}

@Composable
private fun ActionButtonsRow(
    selectedFilter: LibraryFilter?,
    onSelectFilter: (LibraryFilter) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        collectionActions.forEach { action ->
            CollectionActionButton(
                label = action.label,
                selected = action.filter == selectedFilter,
                onClick = { onSelectFilter(action.filter) },
            )
        }
    }
}

@Composable
private fun CollectionActionButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) PrimaryTealDark else PrimaryTeal,
        ),
        onClick = onClick,
    ) {
        LibraryActionButtonContent(label = label)
    }
}

import com.honari.app.domain.model.Book
import com.honari.app.domain.model.Mood

/**
 * UI state for the Discover screen.
 * Single source of truth for the screen's state.
 */
data class DiscoverUiState(
    val isLoading: Boolean = false,
    val featuredBooks: List<Book> = emptyList(),
    val trendingBooks: List<Book> = emptyList(),
    val selectedMood: Mood? = null,
    val error: String? = null
)
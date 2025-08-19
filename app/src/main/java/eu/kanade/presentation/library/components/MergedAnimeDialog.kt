package eu.kanade.presentation.library.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import eu.kanade.presentation.library.anime.AnimeLibraryComfortableGrid
import eu.kanade.presentation.library.anime.AnimeLibraryCompactGrid
import eu.kanade.presentation.library.anime.AnimeLibraryList
import eu.kanade.tachiyomi.ui.library.anime.AnimeLibraryItem
import tachiyomi.domain.library.anime.LibraryAnime
import tachiyomi.domain.library.model.LibraryDisplayMode
import tachiyomi.source.local.entries.anime.isLocal

@Composable
fun MergedAnimeDialog(
    mergedAnime: List<LibraryAnime>,
    onDismissRequest: () -> Unit,
    onAnimeClick: (LibraryAnime) -> Unit,
    displayMode: LibraryDisplayMode,
    columns: Int,
) {
    val animeLibraryItems = remember(mergedAnime) {
        mergedAnime.map { libraryAnime ->
            AnimeLibraryItem(
                libraryAnime = libraryAnime,
                downloadCount = -1,
                unseenCount = libraryAnime.unseenCount,
                isLocal = libraryAnime.anime.isLocal(),
                sourceLanguage = "",
                mergedAnime = mergedAnime,
                isMerged = true,
            )
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        modifier = Modifier.fillMaxWidth(0.85f),
        title = {
            Text(text = "Merged Series")
        },
        text = {
            val screenWidthDp = LocalConfiguration.current.screenWidthDp
            val itemModifier = Modifier

            when (displayMode) {
                LibraryDisplayMode.List -> {
                    AnimeLibraryList(
                        items = animeLibraryItems,
                        entries = columns, // Force single column for list view
                        containerHeight = 0,
                        contentPadding = PaddingValues(4.dp),
                        selection = emptyList(),
                        onClick = onAnimeClick,
                        onLongClick = {},
                        onClickContinueWatching = null,
                        searchQuery = null,
                        onGlobalSearchClicked = {},
                        onMergedItemClick = {},
                        itemModifier = itemModifier,
                    )
                }
                LibraryDisplayMode.CompactGrid, LibraryDisplayMode.CoverOnlyGrid -> {
                    AnimeLibraryCompactGrid(
                        items = animeLibraryItems,
                        showTitle = displayMode is LibraryDisplayMode.CompactGrid,
                        columns = columns,
                        contentPadding = PaddingValues(4.dp),
                        selection = emptyList(),
                        onClick = onAnimeClick,
                        onLongClick = {},
                        onClickContinueWatching = null,
                        searchQuery = null,
                        onGlobalSearchClicked = {},
                        onMergedItemClick = {},
                        itemModifier = itemModifier,
                    )
                }
                LibraryDisplayMode.ComfortableGrid, LibraryDisplayMode.ComfortableGridPanorama -> {
                    AnimeLibraryComfortableGrid(
                        items = animeLibraryItems,
                        columns = columns,
                        contentPadding = PaddingValues(4.dp),
                        selection = emptyList(),
                        onClick = onAnimeClick,
                        onLongClick = {},
                        onClickContinueWatching = null,
                        searchQuery = null,
                        onGlobalSearchClicked = {},
                        onMergedItemClick = {},
                        itemModifier = itemModifier,
                    )
                }
            }
        },
        confirmButton = {}, // No confirm button needed for this dialog
    )
}

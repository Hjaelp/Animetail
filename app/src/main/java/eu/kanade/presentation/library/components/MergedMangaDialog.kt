package eu.kanade.presentation.library.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import eu.kanade.presentation.library.manga.MangaLibraryComfortableGrid
import eu.kanade.presentation.library.manga.MangaLibraryCompactGrid
import eu.kanade.presentation.library.manga.MangaLibraryList
import eu.kanade.tachiyomi.ui.library.manga.MangaLibraryItem
import tachiyomi.domain.library.manga.LibraryManga
import tachiyomi.domain.library.model.LibraryDisplayMode
import tachiyomi.source.local.entries.manga.isLocal

@Composable
fun MergedMangaDialog(
    mergedManga: List<LibraryManga>,
    onDismissRequest: () -> Unit,
    onMangaClick: (LibraryManga) -> Unit,
    displayMode: LibraryDisplayMode,
    columns: Int,
) {
    val mangaLibraryItems = remember(mergedManga) {
        mergedManga.map { libraryManga ->
            MangaLibraryItem(
                libraryManga = libraryManga,
                downloadCount = -1,
                unreadCount = libraryManga.unreadCount,
                isLocal = libraryManga.manga.isLocal(),
                sourceLanguage = "",
                mergedManga = mergedManga,
                isMerged = true,
            )
        }
    }

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
            val itemModifier = Modifier

            when (displayMode) {
                LibraryDisplayMode.List -> {
                    MangaLibraryList(
                        items = mangaLibraryItems,
                        entries = columns, // Force single column for list view
                        containerHeight = 0,
                        contentPadding = PaddingValues(4.dp),
                        selection = emptyList(),
                        onClick = onMangaClick,
                        onLongClick = {},
                        onClickContinueReading = null,
                        searchQuery = null,
                        onGlobalSearchClicked = {},
                        onMergedItemClick = {},
                        itemModifier = itemModifier,
                    )
                }
                LibraryDisplayMode.CompactGrid, LibraryDisplayMode.CoverOnlyGrid -> {
                    MangaLibraryCompactGrid(
                        items = mangaLibraryItems,
                        showTitle = displayMode is LibraryDisplayMode.CompactGrid,
                        columns = columns,
                        contentPadding = PaddingValues(4.dp),
                        selection = emptyList(),
                        onClick = onMangaClick,
                        onLongClick = {},
                        onClickContinueReading = null,
                        searchQuery = null,
                        onGlobalSearchClicked = {},
                        onMergedItemClick = {},
                        itemModifier = itemModifier,
                    )
                }
                LibraryDisplayMode.ComfortableGrid, LibraryDisplayMode.ComfortableGridPanorama -> {
                    MangaLibraryComfortableGrid(
                        items = mangaLibraryItems,
                        columns = columns,
                        contentPadding = PaddingValues(4.dp),
                        selection = emptyList(),
                        onClick = onMangaClick,
                        onLongClick = {},
                        onClickContinueReading = null,
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

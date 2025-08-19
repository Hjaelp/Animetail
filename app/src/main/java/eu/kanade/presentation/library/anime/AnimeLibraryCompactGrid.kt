package eu.kanade.presentation.library.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import eu.kanade.presentation.library.components.DownloadsBadge
import eu.kanade.presentation.library.components.EntryCompactGridItem
import eu.kanade.presentation.library.components.LanguageBadge
import eu.kanade.presentation.library.components.LazyLibraryGrid
import eu.kanade.presentation.library.components.UnviewedBadge
import eu.kanade.presentation.library.components.globalSearchItem
import eu.kanade.presentation.library.components.MergedItemCountBadge
import eu.kanade.tachiyomi.ui.library.anime.AnimeLibraryItem
import tachiyomi.domain.entries.anime.model.AnimeCover
import tachiyomi.domain.library.anime.LibraryAnime

@Composable
fun AnimeLibraryCompactGrid(
    items: List<AnimeLibraryItem>,
    showTitle: Boolean,
    columns: Int,
    contentPadding: PaddingValues,
    selection: List<LibraryAnime>,
    onClick: (LibraryAnime) -> Unit,
    onLongClick: (LibraryAnime) -> Unit,
    onClickContinueWatching: ((LibraryAnime) -> Unit)?,
    searchQuery: String?,
    onGlobalSearchClicked: () -> Unit,
    onMergedItemClick: (List<LibraryAnime>) -> Unit,
    itemModifier: Modifier = Modifier,
) {
    LazyLibraryGrid(
        modifier = Modifier.fillMaxSize(),
        columns = columns,
        contentPadding = contentPadding,
    ) {
        globalSearchItem(searchQuery, onGlobalSearchClicked)

        items(
            items = items,
            contentType = { "anime_library_compact_grid_item" },
        ) { libraryItem ->
            val anime = libraryItem.libraryAnime.anime
            EntryCompactGridItem(
                modifier = itemModifier,
                isSelected = selection.fastAny { it.id == libraryItem.libraryAnime.id },
                title = anime.title.takeIf { showTitle },
                coverData = AnimeCover(
                    animeId = anime.id,
                    sourceId = anime.source,
                    isAnimeFavorite = anime.favorite,
                    url = anime.thumbnailUrl,
                    lastModified = anime.coverLastModified,
                ),
                coverBadgeStart = {
                    DownloadsBadge(count = libraryItem.downloadCount)
                    UnviewedBadge(count = libraryItem.unseenCount)
                },
                coverBadgeEnd = {
                    LanguageBadge(
                        isLocal = libraryItem.isLocal,
                        sourceLanguage = libraryItem.sourceLanguage,
                    )
                },
                mergedItemBadge = if (libraryItem.isMerged && libraryItem.mergedAnime?.size ?: 0 > 1) {
                    { MergedItemCountBadge(count = libraryItem.mergedAnime!!.size) }
                } else {
                    null
                },
                onLongClick = { onLongClick(libraryItem.libraryAnime) },
                onClick = {
                    if (libraryItem.isMerged) {
                        onMergedItemClick(libraryItem.mergedAnime!!)
                    } else {
                        onClick(libraryItem.libraryAnime)
                    }
                },
                onClickContinueViewing = if (onClickContinueWatching != null && libraryItem.unseenCount > 0) {
                    { onClickContinueWatching(libraryItem.libraryAnime) }
                } else {
                    null
                },
                onMergedItemClick = onMergedItemClick,
            )
        }
    }
}

package eu.kanade.presentation.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.entries.components.ItemCover
import tachiyomi.domain.library.anime.LibraryAnime
import tachiyomi.presentation.core.util.secondaryItemAlpha

@Composable
fun MergedAnimeDialog(
    mergedAnime: List<LibraryAnime>,
    onDismissRequest: () -> Unit,
    onAnimeClick: (LibraryAnime) -> Unit,
    onContinueWatching: ((LibraryAnime) -> Unit)? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sortedAnime = remember(mergedAnime) {
        mergedAnime.sortedWith(
            Comparator { a, b ->
                val airingTimeComparison = a.latestUpload.compareTo(b.latestUpload)
                if (airingTimeComparison != 0) {
                    airingTimeComparison
                } else {
                    a.unseenCount.compareTo(b.unseenCount)
                }
            },
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(sortedAnime) { anime ->
                    MergedAnimeItem(
                        libraryAnime = anime,
                        onClick = { onAnimeClick(anime) },
                        onContinueWatching = onContinueWatching?.let { { it(anime) } },
                    )
                }
            }
        }
    }
}

@Composable
private fun MergedAnimeItem(
    libraryAnime: LibraryAnime,
    onClick: () -> Unit,
    onContinueWatching: (() -> Unit)?,
) {
    val shape = RoundedCornerShape(16.dp)
    val hasUnseen = libraryAnime.unseenCount > 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .combinedClickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                ) {
                    ItemCover.Book(
                        data = libraryAnime.anime.thumbnailUrl,
                        modifier = Modifier.height(170.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = libraryAnime.anime.title,
                            modifier = Modifier.padding(end = 28.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        Text(
                            text = "Episodes Seen: ${libraryAnime.seenCount} / ${libraryAnime.totalCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        val description = libraryAnime.anime.description
                        if (!description.isNullOrBlank()) {
                            Text(
                                text = description,
                                modifier = Modifier
                                    .paddingFromBaseline(top = 24.dp)
                                    .secondaryItemAlpha(),
                                maxLines = 5,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }

                if (onContinueWatching != null) {
                    FilledIconButton(
                        onClick = onContinueWatching,
                        enabled = hasUnseen,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Continue watching",
                        )
                    }
                }
            }
        }
    }
}

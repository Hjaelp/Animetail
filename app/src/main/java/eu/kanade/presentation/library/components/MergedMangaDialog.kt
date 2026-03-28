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
import tachiyomi.domain.library.manga.LibraryManga
import tachiyomi.presentation.core.util.secondaryItemAlpha

@Composable
fun MergedMangaDialog(
    mergedManga: List<LibraryManga>,
    onDismissRequest: () -> Unit,
    onMangaClick: (LibraryManga) -> Unit,
    onContinueReading: ((LibraryManga) -> Unit)? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sortedManga = remember(mergedManga) {
        mergedManga.sortedWith(
            Comparator { a, b ->
                val uploadTimeComparison = a.latestUpload.compareTo(b.latestUpload)
                if (uploadTimeComparison != 0) {
                    uploadTimeComparison
                } else {
                    a.unreadCount.compareTo(b.unreadCount)
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
                items(sortedManga) { manga ->
                    MergedMangaItem(
                        libraryManga = manga,
                        onClick = { onMangaClick(manga) },
                        onContinueReading = onContinueReading?.let { { it(manga) } },
                    )
                }
            }
        }
    }
}

@Composable
private fun MergedMangaItem(
    libraryManga: LibraryManga,
    onClick: () -> Unit,
    onContinueReading: (() -> Unit)?,
) {
    val shape = RoundedCornerShape(16.dp)
    val hasUnread = libraryManga.unreadCount > 0

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
                        data = libraryManga.manga.thumbnailUrl,
                        modifier = Modifier.height(170.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = libraryManga.manga.title,
                            modifier = Modifier.padding(end = 28.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        Text(
                            text = "Chapters Read: ${libraryManga.readCount} / ${libraryManga.totalChapters}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        val description = libraryManga.manga.description
                        if (!description.isNullOrBlank()) {
                            Text(
                                text = description,
                                modifier = Modifier
                                    .paddingFromBaseline(top = 24.dp)
                                    .secondaryItemAlpha(),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }

                if (onContinueReading != null) {
                    FilledIconButton(
                        onClick = onContinueReading,
                        enabled = hasUnread,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Continue reading",
                        )
                    }
                }
            }
        }
    }
}

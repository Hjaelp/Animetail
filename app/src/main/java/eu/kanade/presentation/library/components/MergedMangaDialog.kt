package eu.kanade.presentation.library.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tachiyomi.domain.entries.manga.model.MangaCover
import eu.kanade.presentation.entries.components.ItemCover.Book
import tachiyomi.domain.library.manga.LibraryManga

@Composable
fun MergedMangaDialog(
    mergedManga: List<LibraryManga>,
    onDismissRequest: () -> Unit,
    onMangaClick: (LibraryManga) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Merged Series")
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(mergedManga) { libraryManga ->
                    Column(
                        modifier = Modifier
                            .clickable { onMangaClick(libraryManga) }
                            .padding(4.dp),
                    ) {
                        val manga = libraryManga.manga
                        Book(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f / 1.4f),
                            data = MangaCover(
                                mangaId = manga.id,
                                sourceId = manga.source,
                                isMangaFavorite = manga.favorite,
                                url = manga.thumbnailUrl,
                                lastModified = manga.coverLastModified,
                            ),
                        )
                        Text(
                            text = manga.title,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth(),
                            maxLines = 2,
                            minLines = 2,
                            color = MaterialTheme.colorScheme.onSurface
                                .copy(alpha = if (libraryManga.unreadCount == 0L) 0.6f else 1.0f),
                        )
                    }
                }
            }
        },
        confirmButton = {}, // No confirm button needed for this dialog
    )
}

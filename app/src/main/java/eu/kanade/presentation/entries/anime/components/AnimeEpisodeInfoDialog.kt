package eu.kanade.presentation.entries.anime.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import eu.kanade.presentation.components.relativeDateTimeText
import eu.kanade.presentation.util.rememberResourceBitmapPainter
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.player.CustomBookmark
import kotlinx.serialization.json.Json
import tachiyomi.domain.items.episode.model.Episode
import tachiyomi.presentation.core.components.material.SECONDARY_ALPHA
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.concurrent.TimeUnit

@Composable
fun AnimeEpisodeInfoDialog(
    initialEpisodeIndex: Int,
    episodes: List<Episode>,
    animeTitle: String,
    onPlayClick: (Episode) -> Unit,
    onSeenClick: (Episode) -> Unit,
    onBookmarkClick: (Episode) -> Unit,
    onDownloadClick: (Episode) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = initialEpisodeIndex) { episodes.size }
    val json = Injekt.get<Json>()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) { page ->
            val episode = episodes[page]
            val bookmarks = remember(episode.chapterBookmarks) {
                runCatching {
                    episode.chapterBookmarks?.let {
                        json.decodeFromString<List<CustomBookmark>>(it)
                    } ?: emptyList()
                }.getOrDefault(emptyList())
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (episode.thumbnailUrl != null) {
                    AsyncImage(
                        model = episode.thumbnailUrl,
                        contentDescription = null,
                        placeholder = ColorPainter(Color(0x1F888888)),
                        error = rememberResourceBitmapPainter(id = R.drawable.cover_error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = animeTitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                val formattedTitle = if (!episode.name.matches(Regex("^(Episode|Season|Ep\\.)\\s*\\d+$", RegexOption.IGNORE_CASE))) {
                    if (episode.seriesNumber == null || episode.seriesNumber == -1L) {
                        val e = String.format("%02d", episode.episodeNumber.toInt())
                        "Episode $e - ${episode.name}"
                    } else {
                        val s = String.format("%02d", episode.seriesNumber)
                        val e = String.format("%02d", episode.episodeNumber.toInt())
                        "S${s}E${e} - ${episode.name}"
                    }
                } else {
                    episode.name
                }

                Text(
                    text = formattedTitle,
                    style = MaterialTheme.typography.titleLarge,
                )

                val details = mutableListOf<String>()
                if (episode.dateUpload > 0L) {
                    details.add("Uploaded: ${relativeDateTimeText(episode.dateUpload)}")
                }
                if (episode.runtime != null && episode.runtime!! > 0L) {
                    details.add("Runtime: ${formatRuntime(episode.runtime!!)}")
                }

                if (details.isNotEmpty()) {
                    Text(
                        text = details.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { onPlayClick(episode) }) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                        )
                    }
                    IconButton(onClick = { onSeenClick(episode) }) {
                        Icon(
                            imageVector = if (episode.seen) Icons.Filled.Done else Icons.Outlined.Done,
                            contentDescription = null,
                            tint = if (episode.seen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(onClick = { onBookmarkClick(episode) }) {
                        Icon(
                            imageVector = if (episode.bookmark) Icons.Filled.Bookmark else Icons.Outlined.BookmarkAdd,
                            contentDescription = null,
                            tint = if (episode.bookmark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(onClick = { onDownloadClick(episode) }) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = null,
                        )
                    }
                }

                HorizontalDivider()

                val description = episode.description ?: episode.overview
                if (!description.isNullOrBlank()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        text = "No description.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alpha(SECONDARY_ALPHA),
                    )
                }

                if (bookmarks.isNotEmpty()) {
                    HorizontalDivider()
                    Text(
                        text = "Bookmarks",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    bookmarks.forEach { bookmark ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = bookmark.description ?: "No description",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = formatBookmarkTime(bookmark.position),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatBookmarkTime(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format("%d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }
}

private fun formatRuntime(milliseconds: Long): String {
    val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return if (hours > 0) {
        String.format("%dh %02dm", hours, minutes)
    } else {
        String.format("%dm", minutes)
    }
}

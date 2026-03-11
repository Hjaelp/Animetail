package eu.kanade.tachiyomi.ui.player.controls.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import eu.kanade.presentation.entries.components.DotSeparatorText
import eu.kanade.presentation.util.rememberResourceBitmapPainter
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.database.models.anime.Episode
import eu.kanade.tachiyomi.util.lang.toRelativeString
import tachiyomi.domain.entries.anime.model.Anime
import tachiyomi.i18n.aniyomi.AYMR
import tachiyomi.presentation.core.components.VerticalFastScroller
import tachiyomi.presentation.core.components.material.DISABLED_ALPHA
import tachiyomi.presentation.core.components.material.SECONDARY_ALPHA
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.theme.header
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Composable
fun EpisodeListDialog(
    displayMode: Long?,
    currentEpisodeIndex: Int,
    episodeList: List<Episode>,
    dateRelativeTime: Boolean,
    dateFormat: DateTimeFormatter,
    onBookmarkClicked: (Long?, Boolean) -> Unit,
    onFillermarkClicked: (Long?, Boolean) -> Unit,
    onEpisodeClicked: (Long?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val itemScrollIndex = (episodeList.size - currentEpisodeIndex) - 1
    val episodeListState = rememberLazyListState(
        initialFirstVisibleItemIndex = itemScrollIndex,
        initialFirstVisibleItemScrollOffset = -385
    )

    PlayerDialog(
        title = stringResource(AYMR.strings.episodes),
        modifier = Modifier.fillMaxHeight(fraction = 0.8F).fillMaxWidth(fraction = 0.8F),
        onDismissRequest = onDismissRequest,
    ) {
        VerticalFastScroller(
            listState = episodeListState,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                state = episodeListState,
            ) {
                items(
                    items = episodeList.reversed(),
                    key = { "episode-${it.id}" },
                    contentType = { "episode" },
                ) { episode ->

                    val isCurrentEpisode = episode.id == episodeList[currentEpisodeIndex].id

                    val title = if (displayMode == Anime.EPISODE_DISPLAY_NUMBER) {
                        val eStr = String.format("%02d", episode.episode_number.toInt())
                        val numberTitle = stringResource(
                            AYMR.strings.display_mode_episode,
                            eStr,
                        )
                        if (!episode.name.matches(Regex("^(Episode|Season|Ep\\.)\\s*\\d+$", RegexOption.IGNORE_CASE))) {
                            val s = episode.series_number
                            if (s == null || s == -1L) {
                                "Episode $eStr - ${episode.name}"
                            } else {
                                val sStr = String.format("%02d", s)
                                "S${sStr}E${eStr} - ${episode.name}"
                            }
                        } else {
                            numberTitle
                        }
                    } else {
                        episode.name
                    }

                    val date = episode.date_upload
                        .takeIf { it > 0L }
                        ?.let {
                            LocalDate.ofInstant(
                                Instant.ofEpochMilli(it),
                                ZoneId.systemDefault(),
                            ).toRelativeString(
                                context = context,
                                relative = dateRelativeTime,
                                dateFormat = dateFormat,
                            )
                        } ?: ""

                    val watchProgress = episode.last_second_seen
                        .takeIf { !episode.seen && it > 0 }
                        ?.let {
                            stringResource(
                                AYMR.strings.episode_progress,
                                formatTime(it),
                                formatTime(episode.total_seconds),
                            )
                        }

                    val runtime = episode.total_seconds
                        .takeIf { it > 0 }
                        ?.let { formatRuntime(it) }

                    EpisodeListItem(
                        episode = episode,
                        isCurrentEpisode = isCurrentEpisode,
                        title = title,
                        date = date,
                        watchProgress = watchProgress,
                        runtime = runtime,
                        onBookmarkClicked = onBookmarkClicked,
                        onFillermarkClicked = onFillermarkClicked,
                        onEpisodeClicked = onEpisodeClicked,
                    )
                }
            }
        }
    }
}

@Composable
private fun EpisodeListItem(
    episode: Episode,
    isCurrentEpisode: Boolean,
    title: String,
    date: String?,
    watchProgress: String?,
    runtime: String?,
    onBookmarkClicked: (Long?, Boolean) -> Unit,
    onFillermarkClicked: (Long?, Boolean) -> Unit,
    onEpisodeClicked: (Long?) -> Unit,
) {
    var isBookmarked by remember { mutableStateOf(episode.bookmark) }
    var isFillermarked by remember { mutableStateOf(episode.fillermark) }
    var textHeight by remember { mutableStateOf(0) }

    val seen = episode.seen
    val textAlpha = if (seen && !isCurrentEpisode) DISABLED_ALPHA else 1f
    val textWeight = if (isCurrentEpisode) FontWeight.Bold else FontWeight.Normal
    val textStyle = if (isCurrentEpisode) FontStyle.Italic else FontStyle.Normal

    val clickBookmark: () -> Unit = {
        val newState = !isBookmarked
        episode.bookmark = newState
        isBookmarked = newState
        onBookmarkClicked(episode.id, newState)
    }

    val clickFillermark: () -> Unit = {
        val newState = !isFillermarked
        episode.fillermark = newState
        isFillermarked = newState
        onFillermarkClicked(episode.id, newState)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isCurrentEpisode) {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium,
                    )
                } else {
                    Modifier
                },
            )
            .clickable(onClick = { onEpisodeClicked(episode.id) })
            .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val thumbnailUrl = episode.thumbnail_url
        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "",
                placeholder = ColorPainter(Color(0x1F888888)),
                error = rememberResourceBitmapPainter(id = R.drawable.cover_error),
                modifier = Modifier
                    .sizeIn(maxHeight = 110.dp, maxWidth = 200.dp),
            )
            Spacer(modifier = Modifier.padding(start = 8.dp))
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.header,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = LocalContentColor.current.copy(alpha = textAlpha),
                    onTextLayout = { textHeight = it.size.height },
                    fontWeight = textWeight,
                    fontStyle = textStyle,
                )
            }

            val descStyle = MaterialTheme.typography.bodySmall
                .merge(
                    color = LocalContentColor.current
                        .copy(alpha = if (seen && !isCurrentEpisode) DISABLED_ALPHA else SECONDARY_ALPHA),
                )

            val description = episode.overview
            if (description != null) {
                ProvideTextStyle(value = descStyle) {
                    Text(
                        text = description,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            ProvideTextStyle(value = descStyle) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (date != null) {
                        Text(
                            text = "Airdate: $date",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = LocalContentColor.current.copy(alpha = DISABLED_ALPHA)
                        )
                        if (watchProgress != null || runtime != null) DotSeparatorText()
                    }
                    if (watchProgress != null) {
                        Text(
                            text = watchProgress,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = LocalContentColor.current.copy(alpha = DISABLED_ALPHA)
                        )
                        if (runtime != null) DotSeparatorText()
                    }
                    if (runtime != null) {
                        Text(
                            text = "Runtime: $runtime",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = LocalContentColor.current.copy(alpha = DISABLED_ALPHA)
                        )
                    }
                }
            }
        }

        IconButton(onClick = clickBookmark) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkAdd,
                contentDescription = null,
                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(onClick = clickFillermark) {
            Icon(
                imageVector = if (isFillermarked) Icons.AutoMirrored.Filled.Label else Icons.AutoMirrored.Outlined.Label,
                contentDescription = null,
                tint = if (isFillermarked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatTime(milliseconds: Long, useDayFormat: Boolean = false): String {
    return if (useDayFormat) {
        String.format(
            "Airing in %02dd %02dh %02dm %02ds",
            TimeUnit.MILLISECONDS.toDays(milliseconds),
            TimeUnit.MILLISECONDS.toHours(milliseconds) -
                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds)),
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)),
        )
    } else if (milliseconds > 3600000L) {
        String.format(
            "%d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliseconds),
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)),
        )
    } else {
        String.format(
            "%d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)),
        )
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

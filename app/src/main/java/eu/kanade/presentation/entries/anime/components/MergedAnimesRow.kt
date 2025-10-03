package eu.kanade.presentation.entries.anime.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import eu.kanade.presentation.browse.components.AnimeItem
import tachiyomi.domain.entries.anime.model.Anime
import tachiyomi.domain.entries.anime.model.asAnimeCover
import tachiyomi.presentation.core.components.material.padding

@Composable
fun MergedAnimesRow(
    mergedAnimes: List<Anime>,
    getAnimeState: @Composable (Anime) -> State<Anime>,
    onAnimeClick: (Anime) -> Unit,
    onAnimeLongClick: (Anime) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(MaterialTheme.padding.small),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
    ) {
        items(mergedAnimes, key = { "merged-row-${it.url.hashCode()}" }) {
            val anime by getAnimeState(it)
            AnimeItem(
                title = anime.title,
                cover = anime.asAnimeCover(),
                isFavorite = anime.favorite,
                onClick = { onAnimeClick(anime) },
                onLongClick = { onAnimeLongClick(anime) },
                isSelected = false,
            )
        }
    }
}

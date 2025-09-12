package eu.kanade.tachiyomi.data.metadata

import android.app.Application
import android.content.Context
import eu.kanade.tachiyomi.data.metadata.kitsu.Kitsu
import tachiyomi.domain.metadata.anime.repository.AnimeMetadataSource

class MetadataManager(context: Context) {
    val kitsu = Kitsu()

    val sources: List<AnimeMetadataSource> = listOf(
        kitsu,
    )

    val animeSources: List<AnimeMetadataSource> = listOf(
        kitsu,
    )

    fun get(id: Long) = sources.find { it.id == id }
}

package eu.kanade.tachiyomi.data.metadata

import android.app.Application
import android.content.Context
import eu.kanade.tachiyomi.data.metadata.kitsu.Kitsu
import eu.kanade.tachiyomi.data.metadata.jellyfin.Jellyfin
import tachiyomi.domain.metadata.anime.repository.AnimeMetadataSource

class MetadataManager(context: Context) {
    val kitsu = Kitsu()

    private val jellyfinSources: List<Jellyfin> = buildList {
        Jellyfin.getJellyfinSources().forEach { sourceInfo ->
            add(
                Jellyfin(
                    sourceInfo.id + 100,
                    sourceInfo.name,
                    sourceInfo.apiKey,
                    sourceInfo.hostname
                )
            )
        }
    }

    val sources: List<AnimeMetadataSource> = listOf(
        kitsu,
    ) + jellyfinSources

    val animeSources: List<AnimeMetadataSource> = listOf(
        kitsu,
    ) + jellyfinSources

    fun get(id: Long) = sources.find { it.id == id }
}

package eu.kanade.tachiyomi.data.metadata.kitsu

import eu.kanade.tachiyomi.data.metadata.BaseMetadataProvider
import tachiyomi.domain.metadata.anime.model.AnimeMetadata
import tachiyomi.domain.metadata.anime.model.AnimeMetadataSearchResult
import tachiyomi.domain.metadata.anime.model.AnimeEpisode
import eu.kanade.tachiyomi.data.metadata.kitsu.dto.KitsuEpisodeData
import kotlinx.serialization.json.Json
import eu.kanade.tachiyomi.data.metadata.kitsu.dto.KitsuAnimeData
import uy.kohesive.injekt.injectLazy
import kotlin.getValue

import tachiyomi.domain.metadata.anime.repository.AnimeMetadataSource

class Kitsu : BaseMetadataProvider(), AnimeMetadataSource {
    override val id: Long = 1L
    override val name: String = "Kitsu"
    override val supportsSeasonSearch: Boolean = false

    private val json: Json by injectLazy()
    private val kitsuApi: KitsuApi = KitsuApi(client, json)

    override suspend fun searchAnime(query: String): List<AnimeMetadataSearchResult> {
        return kitsuApi.searchAnime(query).data.map { it.toAnimeMetadataSearchResult() }
    }

    override suspend fun searchAnimeSeasons(query: String, parentId: String): List<AnimeMetadataSearchResult> {
        return emptyList()
    }

    override suspend fun getAnimeDetails(id: String): AnimeMetadata? {
        val animeData = kitsuApi.getAnimeDetails(id)
        val episodes = kitsuApi.getAnimeEpisodes(id).map { it.toAnimeEpisode() }
        return animeData.data.toAnimeMetadata(episodes)
    }

    private fun KitsuAnimeData.toAnimeMetadataSearchResult(): AnimeMetadataSearchResult {
        return AnimeMetadataSearchResult(
            id = this.id,
            title = this.attributes.canonicalTitle,
            cover_url = this.attributes.posterImage?.original,
            description = this.attributes.synopsis,
            type = "Anime"
        )
    }

    private fun KitsuAnimeData.toAnimeMetadata(episodes: List<AnimeEpisode>): AnimeMetadata {
        return AnimeMetadata(
            id = this.id,
            title = this.attributes.canonicalTitle,
            synopsis = this.attributes.synopsis,
            coverImage = this.attributes.posterImage?.original,
            posterImage = this.attributes.posterImage?.original,
            episodes = episodes,
        )
    }

    private fun KitsuEpisodeData.toAnimeEpisode(): AnimeEpisode {
        return AnimeEpisode(
            id = this.id,
            number = this.attributes.number,
            title = this.attributes.canonicalTitle,
            synopsis = this.attributes.synopsis,
            thumbnail = this.attributes.thumbnail?.original,
            runtime = this.attributes.length,
            seriesNumber = -1
        )
    }
}

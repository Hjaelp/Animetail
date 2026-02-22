package eu.kanade.tachiyomi.data.metadata.jellyfin

import eu.kanade.tachiyomi.animesource.ConfigurableAnimeSource
import eu.kanade.tachiyomi.animesource.sourcePreferences
import eu.kanade.tachiyomi.data.metadata.BaseMetadataProvider
import eu.kanade.tachiyomi.data.metadata.jellyfin.dto.JellyfinItem
import okhttp3.Dns
import tachiyomi.domain.metadata.anime.model.AnimeEpisode
import tachiyomi.domain.metadata.anime.model.AnimeMetadata
import tachiyomi.domain.metadata.anime.model.AnimeMetadataSearchResult
import tachiyomi.domain.metadata.anime.repository.AnimeMetadataSource
import tachiyomi.domain.source.anime.service.AnimeSourceManager
import uy.kohesive.injekt.injectLazy
import java.security.MessageDigest
import kotlin.getValue

class Jellyfin(
    override val id: Long,
    override val name: String,
    val apiKey: String,
    val baseUrl: String,
) : BaseMetadataProvider(), AnimeMetadataSource {
    override val supportsSeasonSearch: Boolean = true

    val apiClient by lazy {
        networkService.client.newBuilder()
            .addInterceptor(JellyfinInterceptor(apiKey))
            .dns(Dns.SYSTEM) // don't use DNS over HTTPS as it breaks IP addressing
            .build()
    }

    private val jellyfinApi: JellyfinMetadataApi = JellyfinMetadataApi(apiClient, baseUrl)

    override suspend fun searchAnime(query: String): List<AnimeMetadataSearchResult> {
        return jellyfinApi.searchAnime(query).map { it.toAnimeMetadataSearchResult() }
    }

    override suspend fun searchAnimeSeasons(query: String, parentId: String): List<AnimeMetadataSearchResult> {
        return jellyfinApi.searchAnimeSeasons(query, parentId).map { it.toAnimeMetadataSearchResult() }
    }

    override suspend fun getAnimeDetails(id: String): AnimeMetadata? {
        val animeData = jellyfinApi.getAnimeDetails(id)
        val episodes = jellyfinApi.getAnimeEpisodes(id).map { it.toAnimeEpisode() }
        return animeData.toAnimeMetadata(episodes)
    }


    private fun JellyfinItem.toAnimeMetadataSearchResult(): AnimeMetadataSearchResult {
        val tag = this.imageTags?.get("Primary")
        val seriesTag = this.seriesPrimaryImageTag
        val coverUrl = when {
            !tag.isNullOrEmpty() -> "${baseUrl}/Items/${this.id}/Images/Primary?tag=$tag"
            !seriesTag.isNullOrEmpty() && !this.seriesId.isNullOrEmpty() -> "${baseUrl}/Items/${this.seriesId}/Images/Primary?tag=$seriesTag"
            else -> null
        }
        return AnimeMetadataSearchResult(
            id = this.id,
            title = this.name,
            cover_url = coverUrl,
            description = this.overview,
            type = this.type,
        )
    }

    private fun JellyfinItem.toAnimeMetadata(episodes: List<AnimeEpisode>): AnimeMetadata {
        val tag = this.imageTags?.get("Primary")
        val seriesTag = this.seriesPrimaryImageTag
        val coverUrl = when {
            !tag.isNullOrEmpty() -> "${baseUrl}/Items/${this.id}/Images/Primary?tag=$tag"
            !seriesTag.isNullOrEmpty() && !this.seriesId.isNullOrEmpty() -> "${baseUrl}/Items/${this.seriesId}/Images/Primary?tag=$seriesTag"
            else -> null
        }
        return AnimeMetadata(
            id = this.id,
            title = this.name,
            synopsis = this.overview,
            coverImage = coverUrl,
            posterImage = coverUrl,
            episodes = episodes,
        )
    }

    private fun JellyfinItem.toAnimeEpisode(): AnimeEpisode {
        val tag = this.imageTags?.get("Primary")
        val thumbnailUrl = if (!tag.isNullOrEmpty()) {
            "${baseUrl}/Items/${this.id}/Images/Primary?tag=$tag"
        } else {
            null
        }
        return AnimeEpisode(
            id = this.id,
            seriesNumber = this.parentIndexNumber ?: 1L,
            number = this.indexNumber ?: 0,
            title = this.name,
            synopsis = this.overview,
            thumbnail = thumbnailUrl,
            runtime = this.runTimeTicks?.let { it / 600_000_000 }?.toInt(),
        )
    }

    companion object {
        private const val JELLYFIN_VERSION_ID = 1
        const val MAX_JELLYFIN_SOURCES = 10

        private val sourceManager: AnimeSourceManager by injectLazy()

        private fun getSourceId(suffix: Int): Long {
            val key = "jellyfin" + (if (suffix == 1) "" else " ($suffix)") + "/all/$JELLYFIN_VERSION_ID"
            val bytes = MessageDigest.getInstance("MD5").digest(key.toByteArray())
            return (0..7).map { bytes[it].toLong() and 0xff shl 8 * (7 - it) }
                .reduce(Long::or) and Long.MAX_VALUE
        }

        data class JellyfinSourceInfo(
            val id: Long,
            val name: String,
            val hostname: String,
            val apiKey: String,
            val userId: String,
        )

        fun getJellyfinSources(): List<JellyfinSourceInfo> {
            val jellyfinSourcesList = mutableListOf<JellyfinSourceInfo>()
            for (i in 1..MAX_JELLYFIN_SOURCES) {
                val sourceId = getSourceId(i)
                val preferences = (sourceManager.get(sourceId) as? ConfigurableAnimeSource)?.sourcePreferences() ?: continue

                val sourceName = "Jellyfin (${preferences.getString("pref_label", "")})"
                val sourceUserId = preferences.getString("user_id", "")
                val sourceHostUrl = preferences.getString("host_url", "")
                val sourceApiKey = preferences.getString("api_key", "")

                if (sourceUserId.isNullOrEmpty() || sourceHostUrl.isNullOrEmpty() || sourceApiKey.isNullOrEmpty()) {
                    continue
                }

                jellyfinSourcesList.add(JellyfinSourceInfo(i.toLong(), sourceName, sourceHostUrl, sourceApiKey, sourceUserId,))
            }
            return jellyfinSourcesList
        }
    }
}

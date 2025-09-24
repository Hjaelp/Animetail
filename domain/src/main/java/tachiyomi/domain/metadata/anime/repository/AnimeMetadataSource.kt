package tachiyomi.domain.metadata.anime.repository

import tachiyomi.domain.metadata.anime.model.AnimeMetadata
import tachiyomi.domain.metadata.anime.model.AnimeMetadataSearchResult

interface AnimeMetadataSource {
    val id: Long
    val name: String
    val supportsSeasonSearch: Boolean
    suspend fun searchAnime(query: String): List<AnimeMetadataSearchResult>
    suspend fun searchAnimeSeasons(query: String, parentId: String): List<AnimeMetadataSearchResult>
    suspend fun getAnimeDetails(id: String): AnimeMetadata?
}

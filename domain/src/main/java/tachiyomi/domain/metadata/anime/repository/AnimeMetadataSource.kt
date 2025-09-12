package tachiyomi.domain.metadata.anime.repository

import tachiyomi.domain.metadata.anime.model.AnimeMetadata
import tachiyomi.domain.metadata.anime.model.AnimeMetadataSearchResult

interface AnimeMetadataSource {
    val id: Long
    val name: String
    suspend fun searchAnime(query: String): List<AnimeMetadataSearchResult>
    suspend fun getAnimeDetails(id: String): AnimeMetadata?
}

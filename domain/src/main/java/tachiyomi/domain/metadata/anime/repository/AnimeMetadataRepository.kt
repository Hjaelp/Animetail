package tachiyomi.domain.metadata.anime.repository

import tachiyomi.domain.metadata.anime.model.AnimeMetadata
import tachiyomi.domain.metadata.anime.model.AnimeMetadataSearchResult

interface AnimeMetadataRepository {
    suspend fun getAnimeDetails(id: String, providerId: Long): AnimeMetadata?
    suspend fun searchAnime(query: String, providerId: Long): List<AnimeMetadataSearchResult>
}

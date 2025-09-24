package tachiyomi.domain.metadata.anime.interactor

import tachiyomi.domain.metadata.anime.model.AnimeMetadata
import tachiyomi.domain.metadata.anime.model.AnimeMetadataSearchResult
import tachiyomi.domain.metadata.anime.repository.AnimeMetadataRepository

class GetAnimeMetadata(
    private val animeMetadataRepository: AnimeMetadataRepository,
) {
    suspend fun getAnimeDetails(id: String, providerId: Long): AnimeMetadata? {
        return animeMetadataRepository.getAnimeDetails(id, providerId)
    }

    suspend fun searchAnime(query: String, providerId: Long): List<AnimeMetadataSearchResult> {
        return animeMetadataRepository.searchAnime(query, providerId)
    }

    suspend fun searchAnimeSeasons(query: String, providerId: Long, parentId: String): List<AnimeMetadataSearchResult> {
        return animeMetadataRepository.searchAnimeSeasons(query, providerId, parentId)
    }
}

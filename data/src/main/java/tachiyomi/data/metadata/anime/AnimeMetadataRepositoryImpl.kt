package tachiyomi.data.metadata.anime

import tachiyomi.domain.metadata.anime.model.AnimeMetadata
import tachiyomi.domain.metadata.anime.model.AnimeMetadataSearchResult
import tachiyomi.domain.metadata.anime.repository.AnimeMetadataRepository
import tachiyomi.domain.metadata.anime.repository.AnimeMetadataSource

class AnimeMetadataRepositoryImpl(
    private val metadataSources: List<AnimeMetadataSource>,
) : AnimeMetadataRepository {
    override suspend fun getAnimeDetails(id: String, providerId: Long): AnimeMetadata? {
        return metadataSources.firstOrNull { it.id == providerId }?.getAnimeDetails(id)
    }

    override suspend fun searchAnime(query: String, providerId: Long): List<AnimeMetadataSearchResult> {
        return metadataSources.firstOrNull { it.id == providerId }?.searchAnime(query) ?: emptyList()
    }

    override suspend fun searchAnimeSeasons(query: String, providerId: Long, parentId: String): List<AnimeMetadataSearchResult> {
        return metadataSources.firstOrNull { it.id == providerId }?.searchAnimeSeasons(query, parentId) ?: emptyList()
    }
}

package tachiyomi.domain.entries.anime.interactor

import tachiyomi.domain.entries.anime.model.toAnimeUpdate
import tachiyomi.domain.entries.anime.repository.AnimeRepository

class RemoveAnimeMetadataProviderDetails(
    private val animeRepository: AnimeRepository,
) {
    suspend fun await(animeId: Long) {
        val anime = animeRepository.getAnimeById(animeId)

        val updatedAnime = anime.copy(
            metadataProvider = 0, // Clear metadata provider
            metadataProviderAnimeId = null, // Clear metadata provider anime ID
        )
        animeRepository.updateAnime(updatedAnime.toAnimeUpdate())
    }
}

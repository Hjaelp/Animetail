package tachiyomi.domain.metadata.anime.interactor
import tachiyomi.domain.entries.anime.interactor.GetCustomAnimeInfo
import tachiyomi.domain.entries.anime.interactor.SetCustomAnimeInfo
import tachiyomi.domain.entries.anime.model.Anime
import tachiyomi.domain.entries.anime.model.toAnimeUpdate
import tachiyomi.domain.entries.anime.repository.AnimeRepository
import tachiyomi.domain.items.episode.model.Episode
import tachiyomi.domain.items.episode.model.toEpisodeUpdate
import tachiyomi.domain.items.episode.repository.EpisodeRepository
import tachiyomi.domain.metadata.anime.repository.AnimeMetadataRepository

class UpdateAnimeMetadata(
    private val animeMetadataRepository: AnimeMetadataRepository,
    private val animeRepository: AnimeRepository,
    private val episodeRepository: EpisodeRepository,
    private val getCustomAnimeInfo: GetCustomAnimeInfo,
    private val setCustomAnimeInfo: SetCustomAnimeInfo,
) {
    suspend fun await(anime: Anime, providerId: Long, providerAnimeId: String?) {
        val searchId = providerAnimeId ?: anime.metadataProviderAnimeId ?: ""

        val animeMetadata = animeMetadataRepository.getAnimeDetails(searchId, providerId)
        if (animeMetadata != null) {
            val networkEpisodes = animeMetadata.episodes ?: emptyList()
            val localEpisodes = episodeRepository.getEpisodeByAnimeId(anime.id)

            // Helper function to find a matching local episode
            fun findMatchingLocalEpisode(networkEpisodeNumber: Double): Episode? {
                return localEpisodes.find { it.episodeNumber == networkEpisodeNumber }
            }

            val updatedEpisodes = networkEpisodes.mapNotNull { networkEpisode ->
                val networkEpisodeNumber = networkEpisode.number.toDouble()
                findMatchingLocalEpisode(networkEpisodeNumber)?.let { localEpisode ->
                    localEpisode.copy(
                        name = networkEpisode.title ?: localEpisode.name,
                        dateUpload = networkEpisode.airDate ?: localEpisode.dateUpload,
                        episodeNumber = networkEpisodeNumber,
                        description = networkEpisode.synopsis ?: localEpisode.description,
                        seriesNumber = networkEpisode.seriesNumber ?: -1,
                        thumbnailUrl = networkEpisode.thumbnail ?: localEpisode.thumbnailUrl,
                        runtime = networkEpisode.runtime?.toLong()?.times(60 * 1000L) ?: localEpisode.runtime,
                        airDate = networkEpisode.airDate ?: localEpisode.airDate,
                        title = networkEpisode.title ?: localEpisode.title,
                    )
                }
            }

            if (updatedEpisodes.isNotEmpty()) {
                episodeRepository.updateAllEpisodes(updatedEpisodes.map { it.toEpisodeUpdate() })
            }

            // Clear custom genre to show the merged result in the library
            getCustomAnimeInfo.get(anime.id)?.let { customInfo ->
                if (customInfo.genre != null) {
                    setCustomAnimeInfo.set(customInfo.copy(genre = null))
                }
            }

            val mergedGenres = (anime.genre.orEmpty() + animeMetadata.genres.orEmpty())
                .distinct()
                .filter { it.isNotBlank() }
                .takeIf { it.isNotEmpty() }

            val updatedAnime = anime.copy(
                ogDescription = animeMetadata.synopsis,
                ogGenre = mergedGenres,
                /*ogAuthor = animeMetadata.author, // Disabled because 
                ogArtist = animeMetadata.artist,*/ // the current providers don't provide accurate info.
                ogStatus = animeMetadata.status?.toLong() ?: anime.ogStatus,
                thumbnailUrl = animeMetadata.posterImage ?: anime.thumbnailUrl,
                metadataProvider = providerId,
                metadataProviderAnimeId = animeMetadata.id,
            )
            animeRepository.updateAnime(updatedAnime.toAnimeUpdate())
        }
    }
}

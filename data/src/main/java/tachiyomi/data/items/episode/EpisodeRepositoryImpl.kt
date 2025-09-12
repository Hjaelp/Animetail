package tachiyomi.data.items.episode

import kotlinx.coroutines.flow.Flow
import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import tachiyomi.data.handlers.anime.AnimeDatabaseHandler
import tachiyomi.data.items.episode.EpisodeMapper.mapEpisode
import tachiyomi.domain.items.episode.model.Episode
import tachiyomi.domain.items.episode.model.EpisodeUpdate
import tachiyomi.domain.items.episode.repository.EpisodeRepository

class EpisodeRepositoryImpl(
    private val handler: AnimeDatabaseHandler,
) : EpisodeRepository {

    override suspend fun addAllEpisodes(episodes: List<Episode>): List<Episode> {
        return try {
            handler.await(inTransaction = true) {
                episodes.map { episode ->
                    episodesQueries.insert(
                        animeId = episode.animeId,
                        url = episode.url,
                        name = episode.name,
                        scanlator = episode.scanlator,
                        seen = episode.seen,
                        bookmark = episode.bookmark,
                        lastSecondSeen = episode.lastSecondSeen,
                        totalSeconds = episode.totalSeconds,
                        episodeNumber = episode.episodeNumber,
                        seriesNumber = episode.seriesNumber,
                        airDate = episode.airDate,
                        title = episode.title,
                        runtime = episode.runtime,
                        contentRating = null,
                        overview = episode.description,
                        thumbnailUrl = episode.thumbnailUrl,
                        chapterBookmarks = null,
                        sourceOrder = episode.sourceOrder,
                        dateFetch = episode.dateFetch,
                        dateUpload = episode.dateUpload,
                        version = episode.version,
                    )
                    val lastInsertId = episodesQueries.selectLastInsertedRowId().executeAsOne()
                    episode.copy(id = lastInsertId)
                }
            }
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            emptyList()
        }
    }

    override suspend fun updateEpisode(episodeUpdate: EpisodeUpdate) {
        partialUpdate(episodeUpdate)
    }

    override suspend fun updateAllEpisodes(episodeUpdates: List<EpisodeUpdate>) {
        partialUpdate(*episodeUpdates.toTypedArray())
    }

    private suspend fun partialUpdate(vararg episodeUpdates: EpisodeUpdate) {
        handler.await(inTransaction = true) {
            episodeUpdates.forEach { episodeUpdate ->
                episodesQueries.update(
                    animeId = episodeUpdate.animeId,
                    url = episodeUpdate.url,
                    name = episodeUpdate.name,
                    scanlator = episodeUpdate.scanlator,
                    seen = episodeUpdate.seen,
                    bookmark = episodeUpdate.bookmark,
                    lastSecondSeen = episodeUpdate.lastSecondSeen,
                    totalSeconds = episodeUpdate.totalSeconds,
                    episodeNumber = episodeUpdate.episodeNumber,
                    seriesNumber = episodeUpdate.seriesNumber,
                    airDate = episodeUpdate.airDate,
                    title = episodeUpdate.title,
                    runtime = episodeUpdate.runtime,
                    contentRating = null,
                    overview = episodeUpdate.description,
                    thumbnailUrl = episodeUpdate.thumbnailUrl,
                    chapterBookmarks = null,
                    sourceOrder = episodeUpdate.sourceOrder,
                    dateFetch = episodeUpdate.dateFetch,
                    dateUpload = episodeUpdate.dateUpload,
                    episodeId = episodeUpdate.id,
                    version = episodeUpdate.version,
                    isSyncing = 0,
                )
            }
        }
    }

    override suspend fun removeEpisodesWithIds(episodeIds: List<Long>) {
        try {
            handler.await { episodesQueries.removeEpisodesWithIds(episodeIds) }
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }

    override suspend fun getEpisodeByAnimeId(animeId: Long): List<Episode> {
        return handler.awaitList { episodesQueries.getEpisodesByAnimeId(animeId, ::mapEpisode) }
    }

    override suspend fun getBookmarkedEpisodesByAnimeId(animeId: Long): List<Episode> {
        return handler.awaitList {
            episodesQueries.getBookmarkedEpisodesByAnimeId(
                animeId,
                ::mapEpisode,
            )
        }
    }

    override suspend fun getEpisodeById(id: Long): Episode? {
        return handler.awaitOneOrNull { episodesQueries.getEpisodeById(id, ::mapEpisode) }
    }

    override suspend fun getEpisodeByAnimeIdAsFlow(animeId: Long): Flow<List<Episode>> {
        return handler.subscribeToList {
            episodesQueries.getEpisodesByAnimeId(
                animeId,
                ::mapEpisode,
            )
        }
    }

    override suspend fun getEpisodeByUrlAndAnimeId(url: String, animeId: Long): Episode? {
        return handler.awaitOneOrNull {
            episodesQueries.getEpisodeByUrlAndAnimeId(
                url,
                animeId,
                ::mapEpisode,
            )
        }
    }

    private fun mapEpisode(
        id: Long,
        animeId: Long,
        url: String,
        name: String,
        scanlator: String?,
        seen: Boolean,
        bookmark: Boolean,
        lastSecondSeen: Long,
        totalSeconds: Long,
        episodeNumber: Double,
        seriesNumber: Long?,
        airDate: Long?,
        title: String?,
        runtime: Long?,
        contentRating: String?,
        overview: String?,
        thumbnailUrl: String?,
        chapterBookmarks: String?,
        sourceOrder: Long,
        dateFetch: Long,
        dateUpload: Long,
        lastModifiedAt: Long,
        version: Long,
        @Suppress("UNUSED_PARAMETER")
        isSyncing: Long,
    ): Episode = Episode(
        id = id,
        animeId = animeId,
        seen = seen,
        bookmark = bookmark,
        lastSecondSeen = lastSecondSeen,
        totalSeconds = totalSeconds,
        dateFetch = dateFetch,
        sourceOrder = sourceOrder,
        url = url,
        name = name,
        dateUpload = dateUpload,
        episodeNumber = episodeNumber,
        scanlator = scanlator,
        description = overview,
        season = -1L,
        thumbnailUrl = thumbnailUrl,
        lastModifiedAt = lastModifiedAt,
        version = version,
        seriesNumber = seriesNumber,
        airDate = airDate,
        title = title,
        runtime = runtime,
        contentRating = contentRating,
        overview = overview,
        chapterBookmarks = chapterBookmarks,
    )
}

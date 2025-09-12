package eu.kanade.tachiyomi.data.metadata.kitsu.dto

import kotlinx.serialization.Serializable

@Serializable
data class KitsuEpisodeResponse(
    val data: List<KitsuEpisodeData>,
    val links: Links? = null,
)

@Serializable
data class KitsuSingleAnimeResponse(
    val data: KitsuAnimeData,
)

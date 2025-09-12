package eu.kanade.tachiyomi.data.metadata.kitsu.dto

import kotlinx.serialization.Serializable

@Serializable
data class KitsuAnimeResponse(
    val data: List<KitsuAnimeData>,
    val links: Links? = null,
)

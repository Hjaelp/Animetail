package eu.kanade.tachiyomi.data.metadata.kitsu.dto

import kotlinx.serialization.Serializable

@Serializable
data class KitsuEpisodeData(
    val id: String,
    val type: String,
    val attributes: EpisodeAttributes,
)

@Serializable
data class EpisodeAttributes(
    val canonicalTitle: String?,
    val synopsis: String? = null,
    val number: Int,
    val length: Int? = null, // Duration in minutes
    val thumbnail: Image? = null,
)

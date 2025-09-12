package eu.kanade.tachiyomi.data.metadata.kitsu.dto

import kotlinx.serialization.Serializable

@Serializable
data class KitsuAnimeData(
    val id: String,
    val type: String,
    val attributes: AnimeAttributes,
)

@Serializable
data class AnimeAttributes(
    val canonicalTitle: String,
    val synopsis: String? = null,
    val posterImage: Image? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val status: String? = null,
    val subtype: String? = null,
    val episodeCount: Int? = null,
)

@Serializable
data class Image(
    val tiny: String? = null,
    val large: String? = null,
    val small: String? = null,
    val medium: String? = null,
    val original: String? = null,
)

@Serializable
data class Links(
    val self: String? = null,
    val next: String? = null,
    val prev: String? = null,
    val first: String? = null,
    val last: String? = null,
)
package eu.kanade.tachiyomi.data.metadata.kitsu.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class KitsuAnimeResponse(
    val data: List<KitsuAnimeData>,
    val included: List<KitsuIncluded>? = null,
    val links: Links? = null,
)

@Serializable
data class KitsuIncluded(
    val id: String,
    val type: String,
    val attributes: JsonObject,
    val relationships: JsonObject? = null,
)

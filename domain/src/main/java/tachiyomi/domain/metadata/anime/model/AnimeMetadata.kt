package tachiyomi.domain.metadata.anime.model
import tachiyomi.domain.metadata.anime.model.AnimeEpisode

data class AnimeMetadata(
    val id: String,
    val title: String,
    val synopsis: String? = null,
    val coverImage: String? = null,
    val posterImage: String? = null,
    val episodes: List<AnimeEpisode>? = null,
    val genres: List<String>? = null,
    val status: Int? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val type: Int? = null,
    val url: String? = null,
    val source: Int? = null,
)

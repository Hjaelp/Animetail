package tachiyomi.domain.metadata.anime.model

data class AnimeEpisode(
    val id: String,
    val number: Int,
    val title: String? = null,
    val synopsis: String? = null,
    val thumbnail: String? = null,
    val airDate: Long? = null,
    val runtime: Int? = null, // in ms
)

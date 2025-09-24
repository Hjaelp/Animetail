package tachiyomi.domain.metadata.anime.model

data class AnimeMetadataSearchResult(
    val id: String,
    val title: String,
    val cover_url: String?,
    val description: String?,
    val type: String?,
)

package tachiyomi.domain.items.episode.model

data class Episode(
    val id: Long,
    val animeId: Long,
    val seen: Boolean,
    val bookmark: Boolean,
    val lastSecondSeen: Long,
    val totalSeconds: Long,
    val dateFetch: Long,
    val sourceOrder: Long,
    val url: String,
    val name: String,
    val dateUpload: Long,
    val episodeNumber: Double,
    val scanlator: String?,
    val description: String?,
    val season: Long,
    val thumbnailUrl: String?,
    val lastModifiedAt: Long,
    val version: Long,
    val seriesNumber: Long?,
    val airDate: Long?,
    val title: String?,
    val runtime: Long?,
    val contentRating: String?,
    val overview: String?,
    val chapterBookmarks: String?,
) {
    val isRecognizedNumber: Boolean
        get() = episodeNumber >= 0f

    fun copyFrom(other: Episode): Episode {
        return copy(
            name = other.name,
            url = other.url,
            dateUpload = other.dateUpload,
            episodeNumber = other.episodeNumber,
            scanlator = other.scanlator?.ifBlank { null },
            description = other.description,
            season = other.season,
            thumbnailUrl = other.thumbnailUrl,
            seriesNumber = other.seriesNumber,
            airDate = other.airDate,
            title = other.title,
            runtime = other.runtime,
            contentRating = other.contentRating,
            overview = other.overview,
            chapterBookmarks = other.chapterBookmarks,
        )
    }

    companion object {
        fun create() = Episode(
            id = -1,
            animeId = -1,
            seen = false,
            bookmark = false,
            lastSecondSeen = 0,
            totalSeconds = 0,
            dateFetch = 0,
            sourceOrder = 0,
            url = "",
            name = "",
            dateUpload = -1,
            episodeNumber = -1.0,
            scanlator = null,
            description = null,
            season = -1L,
            thumbnailUrl = null,
            lastModifiedAt = 0,
            version = 1,
            seriesNumber = null,
            airDate = null,
            title = null,
            runtime = null,
            contentRating = null,
            overview = null,
            chapterBookmarks = null,
        )
    }
}

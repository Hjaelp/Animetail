package eu.kanade.tachiyomi.data.backup.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import tachiyomi.domain.items.episode.model.Episode

@Serializable
data class BackupEpisode(
    // in 1.x some of these values have different names
    // url is called key in 1.x
    @ProtoNumber(1) var url: String,
    @ProtoNumber(2) var name: String,
    @ProtoNumber(3) var scanlator: String? = null,

    @ProtoNumber(4) var seen: Boolean = false,
    @ProtoNumber(5) var bookmark: Boolean = false,

    // lastPageRead is called progress in 1.x
    @ProtoNumber(6) var lastSecondSeen: Long = 0,
    @ProtoNumber(16) var totalSeconds: Long = 0,
    @ProtoNumber(7) var dateFetch: Long = 0,
    @ProtoNumber(8) var dateUpload: Long = 0,
    // episodeNumber is called number is 1.x
    @ProtoNumber(9) var episodeNumber: Float = 0F,
    @ProtoNumber(10) var sourceOrder: Long = 0,
    @ProtoNumber(11) var lastModifiedAt: Long = 0,
    @ProtoNumber(12) var version: Long = 0,

    // Aniyomi specific values
    @ProtoNumber(501) var fillermark: Boolean = false,
    @ProtoNumber(502) var summary: String? = null,
    @ProtoNumber(503) var previewUrl: String? = null,

    @ProtoNumber(901) var seriesNumber: Long? = null,
    @ProtoNumber(902) var airDate: Long? = null,
    @ProtoNumber(903) var title: String? = null,
    @ProtoNumber(904) var runtime: Long? = null,
    @ProtoNumber(905) var contentRating: String? = null,
    @ProtoNumber(906) var overview: String? = null,
    @ProtoNumber(907) var thumbnailUrl: String? = null,
    @ProtoNumber(908) var chapterBookmarks: String? = null,
) {
    fun toEpisodeImpl(): Episode {
        return Episode.create().copy(
            url = this@BackupEpisode.url,
            name = this@BackupEpisode.name,
            episodeNumber = this@BackupEpisode.episodeNumber.toDouble(),
            scanlator = this@BackupEpisode.scanlator,
            summary = this@BackupEpisode.summary,
            previewUrl = this@BackupEpisode.previewUrl,
            seen = this@BackupEpisode.seen,
            bookmark = this@BackupEpisode.bookmark,
            fillermark = this@BackupEpisode.fillermark,
            lastSecondSeen = this@BackupEpisode.lastSecondSeen,
            totalSeconds = this@BackupEpisode.totalSeconds,
            dateFetch = this@BackupEpisode.dateFetch,
            dateUpload = this@BackupEpisode.dateUpload,
            sourceOrder = this@BackupEpisode.sourceOrder,
            lastModifiedAt = this@BackupEpisode.lastModifiedAt,
            version = this@BackupEpisode.version,
            seriesNumber = this@BackupEpisode.seriesNumber,
            airDate = this@BackupEpisode.airDate,
            title = this@BackupEpisode.title,
            runtime = this@BackupEpisode.runtime,
            contentRating = this@BackupEpisode.contentRating,
            overview = this@BackupEpisode.overview,
            thumbnailUrl = this@BackupEpisode.thumbnailUrl,
            chapterBookmarks = this@BackupEpisode.chapterBookmarks,
        )
    }
}

val backupEpisodeMapper = {
        _: Long,
        _: Long,
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
        _: Long,
        summary: String?,
        previewUrl: String?,
        fillermark: Boolean,
    ->
    BackupEpisode(
        url = url,
        name = name,
        episodeNumber = episodeNumber.toFloat(),
        scanlator = scanlator,
        summary = summary,
        previewUrl = previewUrl,
        seen = seen,
        bookmark = bookmark,
        fillermark = fillermark,
        lastSecondSeen = lastSecondSeen,
        totalSeconds = totalSeconds,
        dateFetch = dateFetch,
        dateUpload = dateUpload,
        sourceOrder = sourceOrder,
        lastModifiedAt = lastModifiedAt,
        version = version,
        seriesNumber = seriesNumber,
        airDate = airDate,
        title = title,
        runtime = runtime,
        contentRating = contentRating,
        overview = overview,
        thumbnailUrl = thumbnailUrl,
        chapterBookmarks = chapterBookmarks,
    )
}

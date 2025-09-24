package eu.kanade.tachiyomi.data.metadata.jellyfin

import eu.kanade.tachiyomi.data.metadata.jellyfin.dto.JellyfinEpisodeResponse
import eu.kanade.tachiyomi.data.metadata.jellyfin.dto.JellyfinItem
import eu.kanade.tachiyomi.data.metadata.jellyfin.dto.JellyfinSearchResponse
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.awaitSuccess
import eu.kanade.tachiyomi.network.parseAs
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import tachiyomi.core.common.util.lang.withIOContext
import uy.kohesive.injekt.injectLazy
import kotlin.getValue

class JellyfinMetadataApi(
    private val client: OkHttpClient,
    private val baseUrl: String,
) {
    private val json: Json by injectLazy()

    suspend fun searchAnime(query: String): List<JellyfinItem> = withIOContext {
        val url = baseUrl.toHttpUrl().newBuilder()
            .addPathSegment("Items")
            .addQueryParameter("Recursive", "true")
            .addQueryParameter("IncludeItemTypes", "Season,Series,Movie") // Search for series
            .addQueryParameter("SearchTerm", query)
            .addQueryParameter("Fields", "Overview,ImageTags")
            .build()

        with(json) {
            client.newCall(GET(url))
                .awaitSuccess()
                .parseAs<JellyfinSearchResponse>()
                .items
        }
    }

    suspend fun searchAnimeSeasons(query: String, parentId: String): List<JellyfinItem> = withIOContext {
        val url = baseUrl.toHttpUrl().newBuilder()
            .addPathSegment("Shows")
            .addPathSegment(parentId)
            .addPathSegment("Seasons")
            .addQueryParameter("SearchTerm", query)
            .addQueryParameter("Fields", "Overview,ImageTags")
            .build()

        with(json) {
            client.newCall(GET(url))
                .awaitSuccess()
                .parseAs<JellyfinSearchResponse>()
                .items
        }
    }

    suspend fun getAnimeDetails(id: String): JellyfinItem = withIOContext {
        val url = baseUrl.toHttpUrl().newBuilder()
            .addPathSegment("items")
            .addPathSegment(id)
            .addQueryParameter("Fields", "Overview,ImageTags")
            .build()

        with(json) {
            client.newCall(GET(url))
                .awaitSuccess()
                .parseAs<JellyfinItem>()
        }
    }

    suspend fun getAnimeEpisodes(seriesId: String): List<JellyfinItem> = withIOContext {
        val url = baseUrl.toHttpUrl().newBuilder()
            .addPathSegment("Items")
            .addQueryParameter("ParentId", seriesId)
            .addQueryParameter("Fields", "Overview,ImageTags,RunTimeTicks")
            .addQueryParameter("EnableImages", "true")
            .addQueryParameter("EnableImageTypes", "Primary,Thumb")
            .build()

        with(json) {
            client.newCall(GET(url))
                .awaitSuccess()
                .parseAs<JellyfinEpisodeResponse>()
                .items
                .sortedBy { it.indexNumber } // Ensure episodes are in order
        }
    }
}

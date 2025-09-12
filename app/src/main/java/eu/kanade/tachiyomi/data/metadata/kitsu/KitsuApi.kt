package eu.kanade.tachiyomi.data.metadata.kitsu

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import eu.kanade.tachiyomi.data.metadata.kitsu.dto.KitsuEpisodeResponse
import eu.kanade.tachiyomi.data.metadata.kitsu.dto.KitsuEpisodeData
import tachiyomi.core.common.util.lang.withIOContext
import eu.kanade.tachiyomi.data.metadata.kitsu.dto.KitsuAnimeResponse
import eu.kanade.tachiyomi.data.metadata.kitsu.dto.KitsuSingleAnimeResponse

class KitsuApi(
    private val client: OkHttpClient,
    private val json: Json
) {

    suspend fun searchAnime(query: String): KitsuAnimeResponse {
        val request = Request.Builder()
            .url("${BASE_URL}anime?filter[text]=$query")
            .build()

        return withIOContext {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    throw Exception("Kitsu API request failed with code ${response.code}")
                }
                val responseBody = response.body?.string() ?: throw Exception("Empty response body")
                json.decodeFromString(KitsuAnimeResponse.serializer(), responseBody)
            }
        }
    }

    suspend fun getAnimeDetails(id: String): KitsuSingleAnimeResponse {
        val request = Request.Builder()
            .url("${BASE_URL}anime/$id")
            .build()

        return withIOContext {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Kitsu API request failed with code ${response.code}")
                }
                val responseBody = response.body?.string() ?: throw Exception("Empty response body")
                json.decodeFromString(KitsuSingleAnimeResponse.serializer(), responseBody)
            }
        }
    }

    suspend fun getAnimeEpisodes(animeId: String): List<KitsuEpisodeData> {
        val allEpisodes = mutableListOf<KitsuEpisodeData>()
        var offset = 0
        val limit = 20 // Max limit for Kitsu API

        while (true) {
            val url = "${BASE_URL}anime/$animeId/episodes?page[offset]=$offset&page[limit]=$limit"
            val request = Request.Builder()
                .url(url)
                .build()

            val response = withIOContext {
                client.newCall(request).execute()
            }
            if (!response.isSuccessful) {
                throw Exception("Kitsu API request failed with code ${response.code}")
            }
            val responseBody = response.body.string()
            val kitsuEpisodeResponse = json.decodeFromString(KitsuEpisodeResponse.serializer(), responseBody)

            if (kitsuEpisodeResponse.data.isEmpty()) {
                break
            }

            allEpisodes.addAll(kitsuEpisodeResponse.data)
            offset += limit

            // Check if there are more pages
            if (kitsuEpisodeResponse.links?.next == null) {
                break
            }
        }
        return allEpisodes
    }

    companion object {
        private const val BASE_URL = "https://kitsu.io/api/edge/"
    }
}

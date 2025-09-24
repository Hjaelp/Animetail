package eu.kanade.tachiyomi.data.metadata.jellyfin

import eu.kanade.tachiyomi.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class JellyfinInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Add the User-Agent header to the original request.
        val uaRequest = originalRequest.newBuilder()
            .header("User-Agent", "Animetail v${BuildConfig.VERSION_NAME} (${BuildConfig.APPLICATION_ID})")
            .build()

        // Check api keys
        if (originalRequest.url.queryParameter("api_key") != null) {
            return chain.proceed(uaRequest)
        }

        val authUrl = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()

        val authRequest = uaRequest.newBuilder().url(authUrl).build()
        return chain.proceed(authRequest)
    }
}

package eu.kanade.tachiyomi.data.metadata

import eu.kanade.tachiyomi.network.NetworkHelper
import okhttp3.OkHttpClient
import uy.kohesive.injekt.injectLazy

abstract class BaseMetadataProvider {

    val networkService: NetworkHelper by injectLazy()

    val client: OkHttpClient
        get() = networkService.client
}

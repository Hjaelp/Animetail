package eu.kanade.tachiyomi.data.track.jellyfin.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JFItem(
    @SerialName("Name") val name: String,
    @SerialName("Id") val id: String,
    @SerialName("UserData") val userData: JFUserData,
    @SerialName("IndexNumber") val indexNumber: Long? = null,
    @SerialName("People") val people: List<JFPerson>? = null,
    @SerialName("SeriesId") val seriesId: String? = null,
    @SerialName("ChildCount") val childCount: Int? = null,
)

@Serializable
data class JFPerson(
    @SerialName("Name") val name: String,
    @SerialName("Id") val id: String,
    @SerialName("Role") val role: String? = null,
    @SerialName("Type") val type: String? = null,
    @SerialName("PrimaryImageTag") val primaryImageTag: String? = null,
)

@Serializable
data class JFUserData(
    @SerialName("Played") val played: Boolean,
)

@Serializable
data class JFItemList(
    @SerialName("Items") val items: List<JFItem>,
)

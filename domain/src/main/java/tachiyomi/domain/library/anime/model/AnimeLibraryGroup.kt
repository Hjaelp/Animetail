package tachiyomi.domain.library.anime.model

import tachiyomi.domain.R

object AnimeLibraryGroup {

    const val BY_DEFAULT = 0
    const val BY_SOURCE = 1
    const val BY_STATUS = 2
    const val BY_TRACK_STATUS = 3
    const val BY_TAG = 4
    const val UNGROUPED = 5
    const val BY_TRACK_SCORE = 6

    fun groupTypeStringRes(type: Int, hasCategories: Boolean = true): Int {
        return when (type) {
            BY_STATUS -> R.string.status
            BY_SOURCE -> R.string.label_sources
            BY_TRACK_STATUS -> R.string.tracking_status
            BY_TAG -> R.string.tag
            UNGROUPED -> R.string.ungrouped
            BY_TRACK_SCORE -> R.string.tracker_score
            else -> if (hasCategories) R.string.categories else R.string.ungrouped
        }
    }
}

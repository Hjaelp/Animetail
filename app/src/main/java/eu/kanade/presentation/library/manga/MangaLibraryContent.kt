package eu.kanade.presentation.library.manga

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import eu.kanade.core.preference.PreferenceMutableState
import eu.kanade.presentation.library.components.LibraryTabs
import eu.kanade.tachiyomi.ui.library.manga.MangaLibraryItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tachiyomi.domain.category.model.Category
import tachiyomi.domain.library.manga.LibraryManga
import tachiyomi.domain.library.model.LibraryDisplayMode
import tachiyomi.presentation.core.components.material.PullRefresh
import kotlin.time.Duration.Companion.seconds

@Composable
fun MangaLibraryContent(
    categories: List<Category>,
    primaryCategories: List<Category>,
    subCategories: List<List<Category>>,
    searchQuery: String?,
    selection: List<LibraryManga>,
    contentPadding: PaddingValues,
    currentPage: () -> Int,
    hasActiveFilters: Boolean,
    showPageTabs: Boolean,
    onChangeCurrentPage: (Int) -> Unit,
    onMangaClicked: (Long) -> Unit,
    onContinueReadingClicked: ((LibraryManga) -> Unit)?,
    onToggleSelection: (LibraryManga) -> Unit,
    onToggleRangeSelection: (LibraryManga) -> Unit,
    onRefresh: (Category?) -> Boolean,
    onGlobalSearchClicked: () -> Unit,
    getNumberOfMangaForCategory: (Category) -> Int?,
    getDisplayMode: (Int) -> PreferenceMutableState<LibraryDisplayMode>,
    getColumnsForOrientation: (Boolean) -> PreferenceMutableState<Int>,
    getLibraryForPage: (Int) -> List<MangaLibraryItem>,
    onMergedItemClick: (List<LibraryManga>) -> Unit,
) {
    Column(
        modifier = Modifier.padding(
            top = contentPadding.calculateTopPadding(),
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
        ),
    ) {
        val coercedCurrentPage = remember { currentPage().coerceAtMost(categories.lastIndex) }
        val pagerState = rememberPagerState(coercedCurrentPage) { categories.size }

        val scope = rememberCoroutineScope()
        var isRefreshing by remember(pagerState.currentPage) { mutableStateOf(false) }

        if (showPageTabs && categories.size > 1) {
            LaunchedEffect(categories) {
                if (categories.size <= pagerState.currentPage) {
                    pagerState.scrollToPage(categories.size - 1)
                }
            }

            if (primaryCategories.isNotEmpty()) {
                val (primaryIndex, subIndex) = remember(pagerState.currentPage, primaryCategories, subCategories) {
                    var count = 0
                    for (i in primaryCategories.indices) {
                        val subSize = subCategories[i].size
                        if (pagerState.currentPage < count + subSize) {
                            return@remember i to (pagerState.currentPage - count)
                        }
                        count += subSize
                    }
                    0 to 0
                }

                LibraryTabs(
                    categories = primaryCategories,
                    currentPageIndex = primaryIndex,
                    getNumberOfItemsForCategory = { primaryCategory ->
                        val pIndex = primaryCategories.indexOf(primaryCategory)
                        if (pIndex != -1) {
                            subCategories[pIndex].sumOf { combinedSubCategory ->
                                getNumberOfMangaForCategory(combinedSubCategory) ?: 0
                            }.takeIf { it > 0 }
                        } else {
                            null
                        }
                    },
                    onTabItemClick = {
                        val targetPage = subCategories.take(it).sumOf { it.size }
                        scope.launch { pagerState.animateScrollToPage(targetPage) }
                    },
                )

                LibraryTabs(
                    categories = subCategories[primaryIndex],
                    currentPageIndex = subIndex,
                    getNumberOfItemsForCategory = { combinedSubCategory ->
                        getNumberOfMangaForCategory(combinedSubCategory)
                    },
                    onTabItemClick = {
                        val primaryStartPage = subCategories.take(primaryIndex).sumOf { it.size }
                        scope.launch { pagerState.animateScrollToPage(primaryStartPage + it) }
                    },
                )
            } else {
                LibraryTabs(
                    categories = categories,
                    currentPageIndex = pagerState.currentPage,
                    getNumberOfItemsForCategory = getNumberOfMangaForCategory,
                ) { scope.launch { pagerState.animateScrollToPage(it) } }
            }
        }

        val notSelectionMode = selection.isEmpty()
        val onClickManga = { manga: LibraryManga ->
            if (notSelectionMode) {
                onMangaClicked(manga.manga.id)
            } else {
                onToggleSelection(manga)
            }
        }

        PullRefresh(
            refreshing = isRefreshing,
            onRefresh = {
                val started = onRefresh(categories[currentPage()])
                if (!started) return@PullRefresh
                scope.launch {
                    // Fake refresh status but hide it after a second as it's a long running task
                    isRefreshing = true
                    delay(1.seconds)
                    isRefreshing = false
                }
            },
            enabled = notSelectionMode,
        ) {
            MangaLibraryPager(
                state = pagerState,
                contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                hasActiveFilters = hasActiveFilters,
                selectedManga = selection,
                searchQuery = searchQuery,
                onGlobalSearchClicked = onGlobalSearchClicked,
                getDisplayMode = getDisplayMode,
                getColumnsForOrientation = getColumnsForOrientation,
                getLibraryForPage = getLibraryForPage,
                onClickManga = onClickManga,
                onLongClickManga = onToggleRangeSelection,
                onClickContinueReading = onContinueReadingClicked,
                onMergedItemClick = onMergedItemClick,
                )
        }

        LaunchedEffect(pagerState.currentPage) {
            onChangeCurrentPage(pagerState.currentPage)
        }
    }
}

package eu.kanade.tachiyomi.ui.metadata

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.metadata.anime.AnimeMetadataSearch
import eu.kanade.presentation.util.Screen
import kotlinx.coroutines.launch
import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.entries.anime.interactor.GetAnime
import tachiyomi.domain.metadata.anime.interactor.UpdateAnimeMetadata
import tachiyomi.domain.metadata.anime.interactor.GetAnimeMetadata
import tachiyomi.domain.metadata.anime.model.AnimeMetadataSearchResult
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MetadataSearchScreen(
    private val animeId: Long,
    private val initialQuery: String,
    private val onDismissRequest: () -> Unit,
    private val providerId: Long,
) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { Model(animeId, initialQuery, providerId) }
        val state by screenModel.state.collectAsState()
        val textFieldState = rememberTextFieldState(initialQuery)

        AnimeMetadataSearch(
            state = textFieldState,
            onDispatchQuery = { screenModel.search(textFieldState.text.toString(), providerId) },
            queryResult = state.results,
            selected = state.selected,
            onSelectedChange = screenModel::updateSelection,
            onConfirmSelection = {
                screenModel.confirmSelection(onDismissRequest)
            },
            onDismissRequest = { navigator.pop() },
            isLoading = state.isLoading,
        )
    }

    private class Model(
        private val animeId: Long,
        initialQuery: String,
        private val providerId: Long,
        private val getAnime: GetAnime = Injekt.get(),
        private val updateAnimeMetadata: UpdateAnimeMetadata = Injekt.get(),
        private val getAnimeMetadata: GetAnimeMetadata = Injekt.get(),
    ) : StateScreenModel<Model.State>(State()) {

        init {
            if (initialQuery.isNotBlank()) {
                search(initialQuery, providerId)
            }
        }

        fun search(query: String, providerId: Long) {
            screenModelScope.launch {
                mutableState.value = state.value.copy(isLoading = true)
                try {
                    val results = getAnimeMetadata.searchAnime(query, providerId)
                    mutableState.value = state.value.copy(results = results, isLoading = false)
                } catch (e: Exception) {
                    logcat(LogPriority.ERROR, e)
                }
            }
        }

        fun updateSelection(selected: AnimeMetadataSearchResult) {
            mutableState.value = state.value.copy(selected = selected)
        }

        fun confirmSelection(onSuccess: () -> Unit) {
            val selected = state.value.selected ?: return
            screenModelScope.launch {
                try {
                    val anime = getAnime.await(animeId)
                    if (anime != null) {
                        updateAnimeMetadata.await(anime, providerId, selected.id)
                        onSuccess()
                    }
                } catch (e: Exception) {
                    logcat(LogPriority.ERROR, e)
                }
            }
        }

        data class State(
            val results: List<AnimeMetadataSearchResult> = emptyList(),
            val isLoading: Boolean = false,
            val selected: AnimeMetadataSearchResult? = null,
        )
    }
}

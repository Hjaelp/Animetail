package eu.kanade.tachiyomi.ui.metadata

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.metadata.anime.AnimeMetadataInfoDialogHome
import eu.kanade.presentation.util.Screen
import kotlinx.coroutines.launch
import tachiyomi.domain.metadata.anime.repository.AnimeMetadataSource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class AnimeMetadataInfoDialogHomeScreen(
    private val animeId: Long,
    private val animeTitle: String,
    private val onDismissRequest: () -> Unit,
    private val onRemoveMetadataProvider: () -> Unit,
) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { Model() }
        val state by screenModel.state.collectAsState()

        AnimeMetadataInfoDialogHome(
            metadataSources = state.metadataSources,
            onNewSearch = {
                navigator.push(
                    MetadataSearchScreen(
                        animeId = animeId,
                        initialQuery = animeTitle,
                        onDismissRequest = onDismissRequest,
                        providerId = it.id,
                    )
                )
            },
            onRemoveMetadataProvider = onRemoveMetadataProvider,
        )
    }

    private class Model(
        private val metadataSources: List<AnimeMetadataSource> = Injekt.get(),
    ) : StateScreenModel<Model.State>(State()) {

        init {
            screenModelScope.launch {
                mutableState.value = State(
                    metadataSources = metadataSources
                )
            }
        }

        data class State(
            val metadataSources: List<AnimeMetadataSource> = emptyList(),
        )
    }
}

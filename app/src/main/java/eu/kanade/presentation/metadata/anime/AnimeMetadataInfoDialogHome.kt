package eu.kanade.presentation.metadata.anime

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tachiyomi.domain.metadata.anime.repository.AnimeMetadataSource

@Composable
fun AnimeMetadataInfoDialogHome(
    metadataSources: List<AnimeMetadataSource>,
    onNewSearch: (AnimeMetadataSource) -> Unit,
    onRemoveMetadataProvider: () -> Unit,
) {
    Column(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Metadata Providers",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        HorizontalDivider()

        metadataSources.forEach { source ->
            MetadataInfoItemEmpty(
                source = source,
                onNewSearch = { onNewSearch(source) },
            )
        }

        Button(
            onClick = onRemoveMetadataProvider,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(text = "Remove current provider")
        }
    }
}

@Composable
private fun MetadataInfoItemEmpty(
    source: AnimeMetadataSource,
    onNewSearch: () -> Unit,
) {
    TextButton(
        onClick = onNewSearch,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = source.name,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

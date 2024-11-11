package eu.kanade.translation

import com.hippo.unifile.UniFile
import eu.kanade.tachiyomi.data.download.manga.MangaDownloadProvider
import eu.kanade.tachiyomi.source.online.HttpSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tachiyomi.domain.entries.manga.interactor.GetManga
import tachiyomi.domain.entries.manga.model.Manga
import tachiyomi.domain.items.chapter.interactor.GetChapter
import tachiyomi.domain.items.chapter.model.Chapter
import tachiyomi.domain.source.manga.service.MangaSourceManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

data class Translation(
    val chapter: Chapter, val manga: Manga,
    val dir: UniFile,
    val saveFile: UniFile,
    private val state: State = State.NOT_TRANSLATED,
) {
    @Transient
    private val _statusFlow = MutableStateFlow(state)

    @Transient
    val statusFlow = _statusFlow.asStateFlow()
    var status: State
        get() = _statusFlow.value
        set(status) {
            _statusFlow.value = status
        }

    enum class State(val value: Int) {
        NOT_TRANSLATED(0),
        TRANSLATING(1),
        TRANSLATED(2),
        ERROR(3),
        QUEUE(4),
    }

    companion object {
        suspend fun fromChapterId(
            chapterId: Long,
            getChapter: GetChapter = Injekt.get(),
            getManga: GetManga = Injekt.get(),
            sourceManager: MangaSourceManager = Injekt.get(),
            downloadProvider: MangaDownloadProvider = Injekt.get(),
            translationProvider: TranslationProvider = Injekt.get(),
            state: State = State.NOT_TRANSLATED,
        ): Translation? {
            val chapter = getChapter.await(chapterId) ?: return null
            val manga = getManga.await(chapter.mangaId) ?: return null
            val source = sourceManager.get(manga.source) as? HttpSource ?: return null
            val dir = downloadProvider.findChapterDir(
                chapter.name,
                chapter.scanlator,
                manga.title,
                source,
            )
            val saveFile = translationProvider.getMangaDir(manga.title, source)
                .createFile(translationProvider.getValidChapterName(chapter.name, chapter.scanlator))
            if (saveFile != null && dir != null) return Translation(chapter, manga, dir, saveFile, state)
            return null
        }

    }
}

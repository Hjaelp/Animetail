package eu.kanade.tachiyomi.ui.player.controls.components.sheets

import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.kanade.tachiyomi.ui.player.CustomBookmark
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import tachiyomi.i18n.aniyomi.AYMR
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun CustomBookmarksSheet(
    customBookmarks: List<CustomBookmark>,
    onAddBookmark: (String?, Int) -> Unit,
    onRemoveBookmark: (CustomBookmark) -> Unit,
    onSeekToBookmark: (CustomBookmark) -> Unit,
    onEditBookmark: (CustomBookmark, String?) -> Unit,
    onDismissRequest: () -> Unit,
    dismissSheet: Boolean,
    currentPosition: Int,
) {
    val customBookmarks: ImmutableList<CustomBookmark> = remember(customBookmarks) {
        customBookmarks.sortedBy { it.position }.toImmutableList()
    }

    var showAddBookmarkDialog by remember { mutableStateOf(false) }
    var showEditBookmarkDialog by remember { mutableStateOf(false) }
    var bookmarkToEdit by remember { mutableStateOf<CustomBookmark?>(null) }

    GenericTracksSheet(
        tracks = customBookmarks,
        header = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TrackSheetTitle(
                    title = stringResource(AYMR.strings.player_sheets_bookmarks_title),
                    modifier = Modifier.padding(top = MaterialTheme.padding.small),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = { showAddBookmarkDialog = true },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(stringResource(AYMR.strings.action_add_bookmark))
                    }
                }
            }
        },
        track = { bookmark ->
            ChapterBookmarkItem(
                bookmark = bookmark,
                onSeekToBookmark = onSeekToBookmark,
                onRemoveBookmark = onRemoveBookmark,
                onEditBookmark = { bookmark ->
                    bookmarkToEdit = bookmark
                    showEditBookmarkDialog = true
                },
            )
        },
        onDismissRequest = onDismissRequest,
        dismissEvent = dismissSheet,
    )

    if (showAddBookmarkDialog) {
        AddBookmarkDialog(
            onAddBookmark = { description, currentPosition ->
                onAddBookmark(description, currentPosition)
                showAddBookmarkDialog = false
            },
            onDismissRequest = { showAddBookmarkDialog = false },
            currentPosition = currentPosition,
        )
    }

    if (showEditBookmarkDialog && bookmarkToEdit != null) {
        EditBookmarkDialog(
            bookmark = bookmarkToEdit!!,
            onEditBookmark = { editedBookmark, newName ->
                onEditBookmark(editedBookmark, newName)
                showEditBookmarkDialog = false
                bookmarkToEdit = null
            },
            onDismissRequest = {
                showEditBookmarkDialog = false
                bookmarkToEdit = null
            },
        )
    }
}

@Composable
fun ChapterBookmarkItem(
    bookmark: CustomBookmark,
    onSeekToBookmark: (CustomBookmark) -> Unit,
    onRemoveBookmark: (CustomBookmark) -> Unit,
    onEditBookmark: (CustomBookmark) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = { onSeekToBookmark(bookmark) },
                ),
        ) {
            Text(
                text = bookmark.description ?: stringResource(AYMR.strings.label_na),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = formatTime(bookmark.position),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = { onEditBookmark(bookmark) }) {
            Icon(Icons.Default.Edit, contentDescription = stringResource(AYMR.strings.action_edit_bookmark))
        }
        IconButton(onClick = { onRemoveBookmark(bookmark) }) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(AYMR.strings.action_remove_bookmark))
        }
    }
}

@Composable
fun AddBookmarkDialog(
    onAddBookmark: (String?, Int) -> Unit,
    onDismissRequest: () -> Unit,
    currentPosition: Int,
) {
    var bookmarkName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(AYMR.strings.player_sheets_bookmarks_title)) },
        text = {
            Column {
                Text(stringResource(AYMR.strings.label_bookmark_description))
                TextField(
                    value = bookmarkName,
                    onValueChange = { bookmarkName = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAddBookmark(bookmarkName.ifEmpty { null }, currentPosition) },
            ) {
                Text(stringResource(MR.strings.action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(MR.strings.action_cancel))
            }
        },
    )
}

fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val seconds = seconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

@Composable
fun EditBookmarkDialog(
    bookmark: CustomBookmark,
    onEditBookmark: (CustomBookmark, String?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var bookmarkName by remember { mutableStateOf(bookmark.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(AYMR.strings.action_edit_bookmark)) },
        text = {
            Column {
                Text(stringResource(AYMR.strings.label_bookmark_description))
                TextField(
                    value = bookmarkName,
                    onValueChange = { bookmarkName = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onEditBookmark(bookmark, bookmarkName.ifEmpty { null }) },
            ) {
                Text(stringResource(MR.strings.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(MR.strings.action_cancel))
            }
        },
    )
}

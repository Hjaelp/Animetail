/*
 * Copyright 2024 Abdallah Mehiz
 * https://github.com/abdallahmehiz/mpvKt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.kanade.tachiyomi.ui.player.controls.components

import eu.kanade.tachiyomi.ui.player.CustomBookmark
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.vivvvek.seeker.Seeker
import dev.vivvvek.seeker.SeekerDefaults
import dev.vivvvek.seeker.Segment
import eu.kanade.tachiyomi.animesource.model.ChapterType
import eu.kanade.tachiyomi.ui.player.controls.LocalPlayerButtonsClickEvent
import `is`.xyz.mpv.Utils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.presentation.core.components.material.padding

@Immutable
data class IndexedSegment(
    val name: String,
    val start: Float,
    val color: Color = Color.Unspecified,
    val index: Int = 0,
    val chapterType: ChapterType = ChapterType.Other,
) {
    companion object {
        val Unspecified = IndexedSegment(name = "", start = 0f)
    }

    fun toSegment(): Segment = Segment(name, start, color)
}

@Composable
fun SeekbarWithTimers(
    position: Float,
    duration: Float,
    readAheadValue: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    timersInverted: Pair<Boolean, Boolean>,
    positionTimerOnClick: () -> Unit,
    durationTimerOnCLick: () -> Unit,
    chapters: ImmutableList<Segment>,
    bookmarks: ImmutableList<CustomBookmark> = persistentListOf(),
    modifier: Modifier = Modifier,
) {
    val clickEvent = LocalPlayerButtonsClickEvent.current
    Row(
        modifier = modifier.height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
    ) {
        VideoTimer(
            value = position,
            timersInverted.first,
            onClick = {
                clickEvent()
                positionTimerOnClick()
            },
            modifier = Modifier.width(92.dp),
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            BookmarkIndicators(
                bookmarks = bookmarks,
                duration = duration,
                modifier = Modifier.fillMaxWidth().padding(bottom = 11.dp).align(Alignment.Center),
            )
            Seeker(
                value = position.coerceIn(0f, duration),
                range = 0f..duration,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                readAheadValue = readAheadValue,
                segments = chapters
                    .filter { it.start in 0f..duration }
                    .let {
                        // add an extra segment at 0 if it doesn't exist.
                        if (it.isNotEmpty() && it.first().start != 0f) {
                            persistentListOf(Segment("", 0f)) + it
                        } else {
                            it
                        }
                    },
                modifier = Modifier.fillMaxWidth(),
                colors = SeekerDefaults.seekerColors(
                    progressColor = MaterialTheme.colorScheme.primary,
                    thumbColor = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.background,
                    readAheadColor = MaterialTheme.colorScheme.inversePrimary,
                ),
            )
        }
        VideoTimer(
            value = if (timersInverted.second) position - duration else duration,
            isInverted = timersInverted.second,
            onClick = {
                clickEvent()
                durationTimerOnCLick()
            },
            modifier = Modifier.width(92.dp),
        )
    }
}

@Composable
fun VideoTimer(
    value: Float,
    isInverted: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    Text(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick,
            )
            .wrapContentHeight(Alignment.CenterVertically),
        text = Utils.prettyTime(value.toInt(), isInverted),
        color = Color.White,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun BookmarkIndicators(
    bookmarks: ImmutableList<CustomBookmark>,
    duration: Float,
    modifier: Modifier = Modifier,
) {
    if (duration == 0f) return

    BoxWithConstraints(modifier = modifier.height(8.dp)) {
        bookmarks.forEach { bookmark ->
            val position = bookmark.position.toFloat() / duration
            if (position in 0f..1f) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp + (this.maxWidth - 18.dp) * position) // Fix later, not very precise
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFe6e6e6)),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSeekBar() {
    SeekbarWithTimers(
        5f,
        20f,
        4f,
        {},
        {},
        Pair(false, true),
        {},
        {},
        persistentListOf(),
        persistentListOf(),
    )
}

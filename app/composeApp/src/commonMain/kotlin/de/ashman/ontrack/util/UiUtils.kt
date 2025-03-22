package de.ashman.ontrack.util

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.Movie

fun Modifier.debugBorder(
    color: Color = Color.Red,
    width: Dp = 1.dp
): Modifier {
    return this.border(width, color)
}

fun fakeItems(): List<Media> = List(20) { Movie(id = "fake_$it", title = "", detailUrl = "") }
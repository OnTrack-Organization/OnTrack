package de.ashman.ontrack.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

// Default Poster Size ist 2:3 Ratio
val DEFAULT_POSTER_HEIGHT = 256.dp
val SMALL_POSTER_HEIGHT = 128.dp
val DEFAULT_POSTER_WIDTH = DEFAULT_POSTER_HEIGHT * (2f / 3f)
val SMALL_POSTER_WIDTH = SMALL_POSTER_HEIGHT * (2f / 3f)

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}
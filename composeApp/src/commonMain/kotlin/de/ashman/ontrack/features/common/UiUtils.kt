package de.ashman.ontrack.features.common

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

// Default Poster Size ist 2:3 Ratio
val DEFAULT_POSTER_HEIGHT = 224.dp
val SMALL_POSTER_HEIGHT = 112.dp

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

fun Modifier.contentSizeAnimation(): Modifier =
    this then Modifier.animateContentSize(
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
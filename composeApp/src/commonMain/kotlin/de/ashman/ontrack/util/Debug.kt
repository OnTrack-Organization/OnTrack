package de.ashman.ontrack.util

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.debugBorder(
    color: Color = Color.Red,
    width: Dp = 1.dp
): Modifier {
    return this.border(width, color)
}

package de.ashman.ontrack.detail.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.ashman.ontrack.media.model.Boardgame

@Composable
fun BoardgameDetailContent(
    boardgame: Boardgame,
) {
    Text(boardgame.toString())
}
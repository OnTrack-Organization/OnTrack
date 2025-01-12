package de.ashman.ontrack.detail.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger
import de.ashman.ontrack.media.model.Boardgame

@Composable
fun BoardgameDetailContent(
    boardgame: Boardgame,
) {
    Logger.i { "HERE: " + boardgame.description.toString() }
    Text(boardgame.toString())
}
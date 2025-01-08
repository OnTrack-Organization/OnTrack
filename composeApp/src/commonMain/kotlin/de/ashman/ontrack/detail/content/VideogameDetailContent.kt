package de.ashman.ontrack.detail.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.ashman.ontrack.media.model.Videogame

@Composable
fun VideogameDetailContent(
    videogame: Videogame,
) {
    Text(videogame.toString())
}
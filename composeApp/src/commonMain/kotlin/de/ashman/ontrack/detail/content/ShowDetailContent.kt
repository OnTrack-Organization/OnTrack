package de.ashman.ontrack.detail.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.ashman.ontrack.media.model.Show

@Composable
fun ShowDetailContent(
    show: Show,
) {
    Text(show.toString())
}
package de.ashman.ontrack.media.show.ui

import de.ashman.ontrack.media.show.model.Show

data class ShowUiState(
    val shows: List<Show> = emptyList(),
)
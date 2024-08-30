package de.ashman.ontrack.show.ui

import de.ashman.ontrack.show.model.Show

data class ShowUiState(
    val shows: List<Show> = emptyList(),
)
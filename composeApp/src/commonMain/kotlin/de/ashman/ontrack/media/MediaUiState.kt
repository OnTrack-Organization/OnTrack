package de.ashman.ontrack.media

import de.ashman.ontrack.media.domain.Media
import de.ashman.ontrack.media.domain.ConsumeStatus

data class MediaUiState<T : Media>(
    val mediaList: List<Media> = emptyList(),

    val selectedMedia: T? = null,
    val statusCounts: Map<ConsumeStatus, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
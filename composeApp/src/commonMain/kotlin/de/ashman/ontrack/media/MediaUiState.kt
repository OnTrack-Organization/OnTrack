package de.ashman.ontrack.media

import de.ashman.ontrack.media.domain.Media
import de.ashman.ontrack.media.domain.StatusType

data class MediaUiState<T : Media>(
    val mediaList: List<T> = emptyList(),
    val selectedMedia: T? = null,
    val statusCounts: Map<StatusType, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
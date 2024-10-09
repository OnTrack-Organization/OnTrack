package de.ashman.ontrack.media

data class MediaUiState<T>(
    val mediaList: List<T> = emptyList(),
    val selectedMedia: T? = null,
    val statusCounts: Map<StatusType, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
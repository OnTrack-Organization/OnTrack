package de.ashman.ontrack.features.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class SharedUiManager() {

    private val _uiState = MutableStateFlow(SharedUiState())
    val uiState: StateFlow<SharedUiState> = _uiState

    fun showSheet(sheet: CurrentSheet) {
        _uiState.update { it.copy(showSheet = true, currentSheet = sheet) }
    }

    fun showSheet() {
        _uiState.update { it.copy(showSheet = true) }
    }

    fun hideSheet() {
        _uiState.update { it.copy(showSheet = false, currentSheet = null) }
    }

    fun hideSheetAndShowSnackbar(message: String) {
        _uiState.update {
            it.copy(
                snackbarMessage = message,
                currentSheet = null,
                showSheet = false,
            )
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    suspend fun showSnackbar(message: StringResource) {
        _uiState.update { it.copy(snackbarMessage = getString(message)) }
    }

}

data class SharedUiState(
    val currentSheet: CurrentSheet? = null,
    val showSheet: Boolean = false,
    val snackbarMessage: String? = null,
)

// TODO add back handling
// Back Handling is being worked on rn
// https://youtrack.jetbrains.com/issue/CMP-4419
enum class CurrentSheet {
    TRACK,
    REVIEW,
    REMOVE,
    FRIEND_ACTIVITY,
    RECOMMEND,
    COMMENTS,
    LIKES,
    FRIENDS,
}
package de.ashman.ontrack.features.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class CommonUiManager() {

    private val _uiState = MutableStateFlow(CommonUiState())
    val uiState: StateFlow<CommonUiState> = _uiState

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
                snackbarMessage = SnackbarEvent(message),
                currentSheet = null,
                showSheet = false,
            )
        }
    }

    suspend fun showSnackbar(message: StringResource) {
        _uiState.update {
            it.copy(snackbarMessage = SnackbarEvent(getString(message)))
        }
    }


    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun resetUiState() {
        _uiState.update { CommonUiState() }
    }
}

data class CommonUiState(
    val currentSheet: CurrentSheet? = null,
    val showSheet: Boolean = false,
    val snackbarMessage: SnackbarEvent<String>? = null,
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
    TIMELINE,
    BLOCK,
    UNBLOCK,
}

class SnackbarEvent<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

package de.ashman.ontrack.features.common

import de.ashman.ontrack.features.detail.tracking.CurrentSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class SharedUiManager() {

    private val _sharedUiState = MutableStateFlow(SharedUiState())
    val sharedUiState: StateFlow<SharedUiState> = _sharedUiState

    fun showBottomSheet(sheet: CurrentSheet) {
        _sharedUiState.update { it.copy(showSheet = true, currentSheet = sheet) }
    }

    fun hideBottomSheet() {
        _sharedUiState.update { it.copy(showSheet = false, currentSheet = null) }
    }

    suspend fun hideSheetAndShowSnackbar(message: StringResource) {
        _sharedUiState.update {
            it.copy(
                snackbarMessage = getString(message),
                currentSheet = null,
                showSheet = false,
            )
        }
    }

    fun clearSnackbarMessage() {
        _sharedUiState.update { it.copy(snackbarMessage = null) }
    }

}

data class SharedUiState(
    val currentSheet: CurrentSheet? = null,
    val showSheet: Boolean = false,
    val snackbarMessage: String? = null,
)
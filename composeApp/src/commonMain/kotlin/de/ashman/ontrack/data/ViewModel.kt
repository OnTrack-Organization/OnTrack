package de.ashman.ontrack.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OrderUiState(pickupOptions = "Hallo"))
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
}

class OrderUiState(
    val pickupOptions: String,
)

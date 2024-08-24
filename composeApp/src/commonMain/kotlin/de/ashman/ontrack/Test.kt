package de.ashman.ontrack

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.ashman.ontrack.data.OrderViewModel
import org.koin.compose.koinInject

@Composable
fun Test(viewModel: OrderViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()

    Text(
        text = state.pickupOptions,
    )
}

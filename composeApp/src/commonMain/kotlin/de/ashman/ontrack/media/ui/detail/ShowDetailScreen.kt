package de.ashman.ontrack.media.ui.detail

import androidx.compose.runtime.Composable
import de.ashman.ontrack.shelf.ui.ShowViewModel
import org.koin.compose.koinInject

@Composable
fun ShowDetailScreen(
    id: String,
    onBack: () -> Unit,
    viewModel: ShowViewModel = koinInject(),
) {

}
package de.ashman.ontrack.features.shelf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType

@Composable
fun OtherUserShelf(
    viewModel: ShelfViewModel,
    userId: String,
    onClickMore: (MediaType) -> Unit,
    onClickItem: (Media) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    ShelfScreen(
        viewModel = viewModel,
        userId = userId,
        onClickMore = onClickMore,
        onClickItem = onClickItem,
        onBack = onBack,
        modifier = modifier,
    )
}
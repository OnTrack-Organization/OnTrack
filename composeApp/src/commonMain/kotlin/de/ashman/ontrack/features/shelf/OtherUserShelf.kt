package de.ashman.ontrack.features.shelf

import androidx.compose.runtime.Composable
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.navigation.MediaNavigationItems

@Composable
fun OtherUserShelf(
    viewModel: ShelfViewModel,
    userId: String,
    onClickMore: (MediaType) -> Unit,
    onClickItem: (MediaNavigationItems) -> Unit,
    onBack: (() -> Unit)? = null,
) {
    ShelfScreen(
        viewModel = viewModel,
        userId = userId,
        onClickMore = onClickMore,
        onClickItem = onClickItem,
        onBack = onBack,
    )
}
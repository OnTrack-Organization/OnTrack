package de.ashman.ontrack.features.shelf

import androidx.compose.runtime.Composable
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.navigation.MediaNavigationItems
import ontrack.app.composeapp.generated.resources.Res
import ontrack.app.composeapp.generated.resources.shelf_other_empty

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
        emptyText = Res.string.shelf_other_empty,
    )
}
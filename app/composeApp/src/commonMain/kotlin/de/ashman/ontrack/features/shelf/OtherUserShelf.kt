package de.ashman.ontrack.features.shelf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.navigation.MediaNavigationItems
import ontrack.app.composeapp.generated.resources.Res
import ontrack.app.composeapp.generated.resources.shelf_other_empty

@Composable
fun OtherUserShelf(
    viewModel: ShelfViewModel,
    sharedUiManager: SharedUiManager,
    userId: String,
    onClickMore: (MediaType) -> Unit,
    onClickItem: (MediaNavigationItems) -> Unit,
    onBack: (() -> Unit)? = null,
) {
    LaunchedEffect(userId) {
        viewModel.setFriendRequestStatus(userId)
    }

    ShelfScreen(
        viewModel = viewModel,
        sharedUiManager = sharedUiManager,
        userId = userId,
        onClickMoreMedia = onClickMore,
        onClickItem = onClickItem,
        onBack = onBack,
        emptyText = Res.string.shelf_other_empty,
    )
}
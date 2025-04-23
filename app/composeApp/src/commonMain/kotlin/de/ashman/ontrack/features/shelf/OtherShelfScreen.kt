package de.ashman.ontrack.features.shelf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.navigation.MediaNavigationParam
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.shelf_other_empty

@Composable
fun OtherShelfScreen(
    viewModel: ShelfViewModel,
    commonUiManager: CommonUiManager,
    userId: String,
    onClickMore: (MediaType) -> Unit,
    onClickItem: (MediaNavigationParam) -> Unit,
    onBack: (() -> Unit)? = null,
) {
    LaunchedEffect(userId) {
        viewModel.setFriendRequestStatus(userId)
    }

    ShelfScreen(
        viewModel = viewModel,
        commonUiManager = commonUiManager,
        userId = userId,
        onClickMoreMedia = onClickMore,
        onClickItem = onClickItem,
        onBack = onBack,
        emptyText = Res.string.shelf_other_empty,
    )
}
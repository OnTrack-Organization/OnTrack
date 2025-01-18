package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaRow
import de.ashman.ontrack.domain.Boardgame
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_franchise
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.BoardgameDetailContent(
    boardgame: Boardgame,
    onClickItem: (String) -> Unit = { },
) {
    item {
        MediaDescription(
            description = boardgame.description,
        )
    }
    item {
        MediaRow(
            title = stringResource(Res.string.detail_franchise),
            otherMedia = boardgame.franchiseItems,
            onClickItem = onClickItem,
        )
    }
}
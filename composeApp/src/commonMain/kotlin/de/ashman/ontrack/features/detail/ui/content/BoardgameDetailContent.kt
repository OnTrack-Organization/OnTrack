package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Media
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_franchise
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.BoardgameDetailContent(
    boardgame: Boardgame,
    onClickItem: (Media) -> Unit = { },
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
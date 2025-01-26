package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaRow
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
            items = boardgame.franchise,
            onClickItem = onClickItem,
        )
    }
}
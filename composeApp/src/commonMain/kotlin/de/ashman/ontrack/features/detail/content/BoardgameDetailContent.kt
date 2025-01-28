package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaPosterRow
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_designer
import ontrack.composeapp.generated.resources.detail_franchise
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.BoardgameDetailContent(
    boardgame: Boardgame,
    onClickItem: (Media) -> Unit = { },
) {
    item {
        CreatorCard(
            title = Res.string.detail_designer,
            name = boardgame.designer?.name,
            imageUrl = boardgame.designer?.imageUrl,
            description = boardgame.designer?.bio,
        )
    }
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = boardgame.description,
        )
    }
    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_franchise),
            items = boardgame.franchise,
            onClickItem = onClickItem,
        )
    }
}

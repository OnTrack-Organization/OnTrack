package de.ashman.ontrack.features.detail.media

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.media.Boardgame
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.detail.components.CreatorCard
import de.ashman.ontrack.features.detail.components.MediaDescription
import de.ashman.ontrack.navigation.MediaNavigationItems
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_designer
import ontrack.composeapp.generated.resources.detail_franchise
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.BoardgameDetailContent(
    boardgame: Boardgame,
    onClickItem: (MediaNavigationItems) -> Unit,
) {
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = boardgame.description,
        )
    }

    boardgame.designer?.let {
        item {
            CreatorCard(
                title = Res.string.detail_designer,
                name = it.name,
                imageUrl = boardgame.designer.imageUrl,
                description = boardgame.designer.bio,
                subInfo = null,
            )
        }
    }

    boardgame.franchise?.let {
        item {
            MediaPosterRow(
                title = stringResource(Res.string.detail_franchise),
                items = it,
                onClickItem = onClickItem,
            )
        }
    }
}

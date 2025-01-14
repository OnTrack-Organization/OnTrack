package de.ashman.ontrack.detail.content

import androidx.compose.runtime.Composable
import de.ashman.ontrack.detail.MediaDescription
import de.ashman.ontrack.detail.MediaRow
import de.ashman.ontrack.media.model.Boardgame
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_franchise
import org.jetbrains.compose.resources.stringResource

@Composable
fun BoardgameDetailContent(
    boardgame: Boardgame,
    onClickItem: (String) -> Unit = { },
) {
    MediaDescription(
        description = boardgame.description,
    )
    MediaRow(
        title = stringResource(Res.string.detail_franchise),
        otherMedia = boardgame.franchiseItems,
        onClickItem = onClickItem,
    )
}
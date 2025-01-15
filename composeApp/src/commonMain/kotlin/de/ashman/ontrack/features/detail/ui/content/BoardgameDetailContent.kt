package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaRow
import de.ashman.ontrack.domain.Boardgame
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
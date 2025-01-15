package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaGenres
import de.ashman.ontrack.features.detail.ui.MediaRow
import de.ashman.ontrack.domain.Show
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_similar
import ontrack.composeapp.generated.resources.media_shows
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShowDetailContent(
    show: Show,
    onClickItem: (String) -> Unit = { },
) {
    MediaDescription(show.description)

    MediaGenres(show.genres)

    MediaRow(
        title = stringResource(Res.string.detail_similar, stringResource(Res.string.media_shows)),
        otherMedia = show.similarShows,
        onClickItem = onClickItem,
    )
}
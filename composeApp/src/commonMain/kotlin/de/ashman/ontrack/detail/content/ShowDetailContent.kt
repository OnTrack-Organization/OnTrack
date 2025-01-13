package de.ashman.ontrack.detail.content

import androidx.compose.runtime.Composable
import de.ashman.ontrack.detail.MediaDescription
import de.ashman.ontrack.detail.MediaGenres
import de.ashman.ontrack.detail.SimilarMedia
import de.ashman.ontrack.media.model.Show
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.media_shows
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShowDetailContent(
    show: Show,
    onClickItem: (Show) -> Unit = { },
) {
    MediaDescription(show.overview)

    MediaGenres(show.genres)

    SimilarMedia(
        mediaString = stringResource(Res.string.media_shows),
        similarMedia = show.similarShows,
        onClickItem = { onClickItem(show) },
    )
}
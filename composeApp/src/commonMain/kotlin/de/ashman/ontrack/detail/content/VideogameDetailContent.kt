package de.ashman.ontrack.detail.content

import androidx.compose.runtime.Composable
import de.ashman.ontrack.detail.MediaDescription
import de.ashman.ontrack.detail.MediaGenres
import de.ashman.ontrack.detail.MediaRow
import de.ashman.ontrack.media.model.Videogame
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_similar
import ontrack.composeapp.generated.resources.media_videogames
import org.jetbrains.compose.resources.stringResource

@Composable
fun VideogameDetailContent(
    videogame: Videogame,
    onClickItem: (String) -> Unit = { },
) {
    MediaDescription(videogame.description)

    MediaGenres(videogame.genres)

    MediaRow(
        title = stringResource(Res.string.detail_similar, stringResource(Res.string.media_videogames)),
        otherMedia = videogame.similarGames,
        onClickItem = onClickItem,
    )
}
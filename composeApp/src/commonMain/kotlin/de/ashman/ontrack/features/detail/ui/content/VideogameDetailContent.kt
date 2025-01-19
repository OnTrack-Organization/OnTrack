package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Videogame
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaGenres
import de.ashman.ontrack.features.detail.ui.MediaRow
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_similar_videogames
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.VideogameDetailContent(
    videogame: Videogame,
    onClickItem: (String) -> Unit = { },
) {
    item {
        MediaDescription(videogame.description)
    }
    item {
        MediaGenres(videogame.genres)
    }
    item {
        MediaRow(
            title = stringResource(Res.string.detail_similar_videogames),
            otherMedia = videogame.similarGames,
            onClickItem = onClickItem,
        )
    }
}
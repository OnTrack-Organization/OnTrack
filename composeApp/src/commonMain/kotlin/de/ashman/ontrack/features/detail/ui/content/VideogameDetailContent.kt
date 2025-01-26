package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Videogame
import de.ashman.ontrack.features.common.MediaChips
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaRow
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_genres
import ontrack.composeapp.generated.resources.detail_platforms
import ontrack.composeapp.generated.resources.detail_similar_videogames
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.VideogameDetailContent(
    videogame: Videogame,
    onClickItem: (Media) -> Unit = { },
) {
    item {
        MediaDescription(videogame.description)
    }
    item {
        MediaChips(
            title = stringResource(Res.string.detail_platforms),
            items = videogame.platforms?.map { it.name },
        )
    }
    item {
        MediaChips(
            title = stringResource(Res.string.detail_genres),
            items = videogame.genres,
        )
    }
    item {
        MediaRow(
            title = stringResource(Res.string.detail_similar_videogames),
            items = videogame.similarGames,
            onClickItem = onClickItem,
        )
    }
}
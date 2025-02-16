package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Videogame
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.detail.components.MediaChips
import de.ashman.ontrack.features.detail.components.MediaDescription
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_franchise
import ontrack.composeapp.generated.resources.detail_genres
import ontrack.composeapp.generated.resources.detail_platforms
import ontrack.composeapp.generated.resources.detail_similar
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.VideogameDetailContent(
    videogame: Videogame,
    onClickItem: (MediaNavigationItems) -> Unit,
) {
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = videogame.description,
        )
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
        MediaPosterRow(
            title = stringResource(Res.string.detail_franchise),
            items = videogame.franchises,
        )
    }

    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_similar, pluralStringResource(videogame.mediaType.getMediaTypeUi().title, 2)),
            items = videogame.similarGames,
            onClickItem = onClickItem,
        )
    }
}

package de.ashman.ontrack.features.detail.media

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.media.Videogame
import de.ashman.ontrack.features.common.MediaImageRow
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.detail.components.MediaChips
import de.ashman.ontrack.features.detail.components.MediaDescription
import de.ashman.ontrack.navigation.MediaNavigationParam
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
    onClickItem: (MediaNavigationParam) -> Unit,
    onClickImage: (String) -> Unit,
) {
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = videogame.description,
        )
    }

    videogame.getPlatformNames()?.let {
        item {
            MediaChips(
                title = stringResource(Res.string.detail_platforms),
                items = it,
            )
        }
    }

    videogame.genres?.let {
        item {
            MediaChips(
                title = stringResource(Res.string.detail_genres),
                items = it,
            )
        }
    }

    videogame.franchises?.let {
        item {
            MediaPosterRow(
                title = stringResource(Res.string.detail_franchise),
                items = it,
            )
        }
    }

    videogame.similarGames?.let {
        item {
            MediaPosterRow(
                title = stringResource(Res.string.detail_similar, pluralStringResource(videogame.mediaType.getMediaTypeUi().title, 2)),
                items = it,
                onClickItem = onClickItem,
            )
        }
    }

    videogame.screenshots?.let {
        item {
            MediaImageRow(
                images = it,
                onClickImage = onClickImage,
            )
        }
    }
}

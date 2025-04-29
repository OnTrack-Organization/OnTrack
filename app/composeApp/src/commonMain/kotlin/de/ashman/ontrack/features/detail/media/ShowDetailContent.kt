package de.ashman.ontrack.features.detail.media

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.api.utils.getLivingDates
import de.ashman.ontrack.domain.media.Show
import de.ashman.ontrack.features.common.MediaImageRow
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.detail.components.CreatorCard
import de.ashman.ontrack.features.detail.components.MediaChips
import de.ashman.ontrack.features.detail.components.MediaDescription
import de.ashman.ontrack.navigation.MediaNavigationParam
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_director
import ontrack.composeapp.generated.resources.detail_genres
import ontrack.composeapp.generated.resources.detail_seasons
import ontrack.composeapp.generated.resources.detail_similar
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.ShowDetailContent(
    show: Show,
    onClickItem: (MediaNavigationParam) -> Unit,
    onClickImage: (String) -> Unit,
) {
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = show.description,
        )
    }

    show.director?.let {
        item {
            CreatorCard(
                title = Res.string.detail_director,
                name = it.name,
                subInfo = getLivingDates(it.birthDate, it.deathDate),
                imageUrl = it.imageUrl,
                description = it.bio,
            )
        }
    }

    show.genres?.let {
        item {
            MediaChips(
                title = stringResource(Res.string.detail_genres),
                items = it,
            )
        }
    }

    show.seasons?.let {
        item {
            MediaPosterRow(
                title = pluralStringResource(Res.plurals.detail_seasons, it.size, it.size),
                items = it,
            )
        }
    }

    show.similarShows?.let {
        item {
            MediaPosterRow(
                title = stringResource(Res.string.detail_similar, pluralStringResource(show.mediaType.getMediaTypeUi().title, 2)),
                items = it,
                onClickItem = onClickItem,
            )
        }
    }

    show.images?.let {
        item {
            MediaImageRow(
                images = it,
                onClickImage = onClickImage,
            )
        }
    }
}
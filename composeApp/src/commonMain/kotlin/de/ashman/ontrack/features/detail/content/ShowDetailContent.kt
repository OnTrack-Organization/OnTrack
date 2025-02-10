package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.api.utils.getLivingDates
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.detail.components.CreatorCard
import de.ashman.ontrack.features.detail.components.MediaChips
import de.ashman.ontrack.features.detail.components.MediaDescription
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_director
import ontrack.composeapp.generated.resources.detail_genres
import ontrack.composeapp.generated.resources.detail_seasons
import ontrack.composeapp.generated.resources.detail_similar_shows
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.ShowDetailContent(
    show: Show,
    onClickItem: (String) -> Unit,
) {
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = show.description,
        )
    }

    item {
        CreatorCard(
            title = Res.string.detail_director,
            name = show.director?.name,
            subInfo = getLivingDates(show.director?.birthDate, show.director?.deathDate),
            imageUrl = show.director?.imageUrl,
            description = show.director?.bio,
        )
    }

    item {
        MediaChips(
            title = stringResource(Res.string.detail_genres),
            items = show.genres,
        )
    }

    item {
        show.numberOfSeasons?.let {
            MediaPosterRow(
                title = pluralStringResource(Res.plurals.detail_seasons, show.numberOfSeasons, show.numberOfSeasons),
                items = show.seasons,
            )
        }
    }

    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_similar_shows),
            items = show.similarShows,
            onClickItem = onClickItem,
        )
    }
}
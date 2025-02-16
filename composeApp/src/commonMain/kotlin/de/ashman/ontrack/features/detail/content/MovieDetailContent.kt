package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.api.utils.getLivingDates
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.detail.components.CreatorCard
import de.ashman.ontrack.features.detail.components.MediaChips
import de.ashman.ontrack.features.detail.components.MediaDescription
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_collection
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_director
import ontrack.composeapp.generated.resources.detail_genres
import ontrack.composeapp.generated.resources.detail_similar
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.MovieDetailContent(
    movie: Movie,
    onClickItem: (MediaNavigationItems) -> Unit,
) {
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = movie.description,
        )
    }

    item {
        CreatorCard(
            title = Res.string.detail_director,
            name = movie.director?.name,
            subInfo = getLivingDates(movie.director?.birthDate, movie.director?.deathDate),
            imageUrl = movie.director?.imageUrl,
            description = movie.director?.bio,
        )
    }

    item {
        MediaChips(
            title = stringResource(Res.string.detail_genres),
            items = movie.genres,
        )
    }

    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_collection),
            items = movie.collection?.movies,
            onClickItem = onClickItem,
        )
    }

    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_similar, pluralStringResource(movie.mediaType.getMediaTypeUi().title, 2)),
            items = movie.similarMovies,
            onClickItem = onClickItem,
        )
    }
}

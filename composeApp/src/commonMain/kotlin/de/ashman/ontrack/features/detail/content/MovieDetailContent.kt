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

    movie.director?.let {
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

    movie.genres?.let {
        item {
            MediaChips(
                title = stringResource(Res.string.detail_genres),
                items = it,
            )
        }
    }

    movie.collection?.movies?.let {
        item {
            MediaPosterRow(
                title = stringResource(Res.string.detail_collection),
                items = it,
                onClickItem = onClickItem,
            )
        }
    }

    movie.similarMovies?.let {
        item {
            MediaPosterRow(
                title = stringResource(Res.string.detail_similar, pluralStringResource(movie.mediaType.getMediaTypeUi().title, 2)),
                items = it,
                onClickItem = onClickItem,
            )
        }
    }
}

package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.api.utils.getLivingDates
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.features.common.CreatorCard
import de.ashman.ontrack.features.common.MediaChips
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaPosterRow
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_collection
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_director
import ontrack.composeapp.generated.resources.detail_genres
import ontrack.composeapp.generated.resources.detail_similar_movies
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.MovieDetailContent(
    movie: Movie,
    onClickItem: (Media) -> Unit,
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
            title = stringResource(Res.string.detail_similar_movies),
            items = movie.similarMovies,
            onClickItem = onClickItem,
        )
    }
}

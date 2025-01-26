package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.features.common.MediaChips
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaRow
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_genres
import ontrack.composeapp.generated.resources.detail_similar_movies
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.MovieDetailContent(
    movie: Movie,
    onClickItem: (Media) -> Unit,
) {
    item {
        MediaChips(
            title = stringResource(Res.string.detail_genres),
            items = movie.genres,
        )
    }
    item {
        MediaDescription(movie.description)
    }
    item {
        MediaRow(
            title = stringResource(Res.string.detail_similar_movies),
            items = movie.similarMovies,
            onClickItem = onClickItem,
        )
    }
}
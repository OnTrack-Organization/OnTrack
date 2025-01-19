package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaGenres
import de.ashman.ontrack.features.detail.ui.MediaRow
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_similar_movies
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.MovieDetailContent(
    movie: Movie,
    onClickItem: (String) -> Unit = { },
) {
    item {
        MediaGenres(movie.genres)
    }
    item {
        MediaDescription(movie.description)
    }
    item {
        MediaRow(
            title = stringResource(Res.string.detail_similar_movies),
            otherMedia = movie.similarMovies,
            onClickItem = onClickItem,
        )
    }
}
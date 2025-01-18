package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaGenres
import de.ashman.ontrack.features.detail.ui.MediaRow
import de.ashman.ontrack.domain.Movie
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_similar
import ontrack.composeapp.generated.resources.media_movies
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
            title = stringResource(Res.string.detail_similar, stringResource(Res.string.media_movies)),
            otherMedia = movie.similarMovies,
            onClickItem = onClickItem,
        )
    }
}
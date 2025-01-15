package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaGenres
import de.ashman.ontrack.features.detail.ui.MediaRow
import de.ashman.ontrack.domain.Movie
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_similar
import ontrack.composeapp.generated.resources.media_movies
import org.jetbrains.compose.resources.stringResource

@Composable
fun MovieDetailContent(
    movie: Movie,
    onClickItem: (String) -> Unit = { },
) {
    MediaDescription(movie.description)

    MediaGenres(movie.genres)

    MediaRow(
        title = stringResource(Res.string.detail_similar, stringResource(Res.string.media_movies)),
        otherMedia = movie.similarMovies,
        onClickItem = onClickItem,
    )
}
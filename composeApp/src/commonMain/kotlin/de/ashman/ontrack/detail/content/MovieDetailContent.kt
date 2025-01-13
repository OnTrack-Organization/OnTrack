package de.ashman.ontrack.detail.content

import androidx.compose.runtime.Composable
import de.ashman.ontrack.detail.MediaDescription
import de.ashman.ontrack.detail.MediaGenres
import de.ashman.ontrack.detail.SimilarMedia
import de.ashman.ontrack.media.model.Movie
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.media_movies
import org.jetbrains.compose.resources.stringResource

@Composable
fun MovieDetailContent(
    movie: Movie,
    onClickItem: (Movie) -> Unit = { },
) {
    MediaDescription(movie.overview)

    MediaGenres(movie.genres)

    SimilarMedia(
        mediaString = stringResource(Res.string.media_movies),
        similarMedia = movie.similarMovies,
        onClickItem = { onClickItem(movie) },
    )
}
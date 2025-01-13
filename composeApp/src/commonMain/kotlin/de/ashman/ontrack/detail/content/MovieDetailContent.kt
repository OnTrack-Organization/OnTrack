package de.ashman.ontrack.detail.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.detail.MediaDescription
import de.ashman.ontrack.detail.MediaGenres
import de.ashman.ontrack.detail.MediaPoster
import de.ashman.ontrack.detail.SimilarMedia
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.media.model.Movie
import de.ashman.ontrack.util.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.util.SMALL_POSTER_WIDTH
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_genres
import ontrack.composeapp.generated.resources.detail_similar
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
        similarMedia = movie.similarMovies,
        onClickItem = { onClickItem(movie) },
    )
}
package de.ashman.ontrack.xyz

import de.ashman.ontrack.media.movie.model.domain.Movie
import de.ashman.ontrack.media.show.model.Show

sealed class Media {
    data class MovieMedia(val movie: Movie) : Media()
    data class ShowMedia(val show: Show) : Media()
}

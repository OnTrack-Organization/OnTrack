package de.ashman.ontrack.shelf.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.domain.MediaType
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.api.movie.MovieRepository
import de.ashman.ontrack.media.domain.Movie

class MovieViewModel(
    private val repository: MovieRepository,
    private val userService: UserService,
) : MediaViewModel<Movie>(repository, userService) {

    init {
        fetchMediaByQuery("lion")
        fetchStatusCounts(MediaType.MOVIE)
    }
}

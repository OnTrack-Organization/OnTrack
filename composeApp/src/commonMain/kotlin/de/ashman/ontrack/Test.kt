package de.ashman.ontrack

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.ashman.ontrack.movie.ui.MovieViewModel
import de.ashman.ontrack.show.ui.ShowViewModel
import org.koin.compose.koinInject

@Composable
fun Test(
    movieViewModel: MovieViewModel = koinInject(),
    showViewModel: ShowViewModel = koinInject()
) {
    val movieState by movieViewModel.uiState.collectAsState()
    val showState by showViewModel.uiState.collectAsState()

    Button(
        onClick = movieViewModel::fetchPopular
    ) {

    }
    if (movieState.movies.isNotEmpty())
        Text(
            text = "${movieState.movies.first()}",
        )

    if (showState.shows.isNotEmpty())
        Text(
            text = "${showState.shows.first()}",
        )
}

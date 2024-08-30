package de.ashman.ontrack

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.ashman.ontrack.movie.ui.MovieViewModel
import org.koin.compose.koinInject

@Composable
fun Test(viewModel: MovieViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()

    Button(
        onClick = viewModel::fetchPopular
    ) {

    }
    if (state.movies.isNotEmpty())
    Text(
        text = "${state.movies.first()}",
    )
}

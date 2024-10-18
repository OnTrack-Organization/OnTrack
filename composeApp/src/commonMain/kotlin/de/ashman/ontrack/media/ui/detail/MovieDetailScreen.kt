package de.ashman.ontrack.media.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import de.ashman.ontrack.media.ui.StarRating
import de.ashman.ontrack.media.domain.Movie
import de.ashman.ontrack.media.domain.StatusType
import de.ashman.ontrack.shelf.MediaType
import de.ashman.ontrack.shelf.ui.MovieViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    id: String,
    onBack: () -> Unit,
    viewModel: MovieViewModel = koinInject(),
) {
    // TODO the functionality should be
    // adding to list, add status, add review, add rating
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(id) {
        viewModel.fetchMediaDetails(id)
    }

    MovieDetailContent(
        onBack = onBack,
        onChangeStatus = { statusType ->
            // TODO change this abomination
            val newMovie = uiState.selectedMedia?.copy(consumeStatus = statusType)
            // 1. update ui
            viewModel.updateSelectedUi(newMovie)
            // 2. update database instance
            viewModel.updateSelectedDb(newMovie)
        },
        onChangeRating = { rating ->
            val newMovie = uiState.selectedMedia?.copy(userRating = rating)
            viewModel.updateSelectedUi(newMovie)
            viewModel.updateSelectedDb(newMovie)
        },
        movie = uiState.selectedMedia
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailContent(
    onBack: () -> Unit = {},
    onChangeStatus: (StatusType) -> Unit = {},
    onChangeRating: (Float) -> Unit = {},
    movie: Movie?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, "") }
                    )
                }
            )
        }
    ) { innerPadding ->
        if (movie != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TitleContainer(movie)
                Text(movie.overview ?: "N/A")

                ConsumeStatusRow(
                    mediaType = MediaType.MOVIES,
                    currentStatus = movie.consumeStatus,
                    onChangeStatus = onChangeStatus,
                )

                StarRating(
                    rating = movie.userRating,
                    onRatingChanged = onChangeRating
                )
            }
        } else {
            // TODO change
            Text("Loading...")
        }
    }
}

@Composable
fun ConsumeStatusRow(
    mediaType: MediaType,
    currentStatus: StatusType?,
    onChangeStatus: (StatusType) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        mediaType.statusTypes.forEach { statusType ->
            val isSelected = currentStatus == statusType
            val icon = MediaType.MOVIES.getStatusIcon(statusType, isSelected)

            IconButton(
                onClick = { onChangeStatus(statusType) },
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = statusType.name
                )
            }
        }
    }
}

@Composable
fun TitleContainer(
    movie: Movie
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            modifier = Modifier.width(200.dp).border(BorderStroke(3.dp, Color.Red)),
            model = movie.coverUrl,
            contentDescription = "Cover",
            contentScale = ContentScale.Fit
        )
        Column {
            Text(text = movie.name, style = MaterialTheme.typography.displaySmall)
            movie.releaseDate?.let { Text(text = it, style = MaterialTheme.typography.bodyLarge) }
            movie.runtime?.let { Text(it.toString()) }
            Text("${movie.voteAverage} / ${movie.voteCount}")
        }
    }
}
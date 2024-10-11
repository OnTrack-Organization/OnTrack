package de.ashman.ontrack.media.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import de.ashman.ontrack.media.ui.StarRating
import de.ashman.ontrack.media.domain.Movie
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
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            uiState.selectedMedia?.let { TitleContainer(it) }
            uiState.selectedMedia?.overview?.let { Text(it) }

            ConsumeStatusRow(MediaType.MOVIES)

            uiState.selectedMedia?.userRating?.let {
                StarRating(
                    rating = it,
                    onRatingChanged = {
                        // TODO viewmodel change rating callback
                    }
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

@Composable
fun ConsumeStatusRow(
    mediaType: MediaType,
    onChangeStatus: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        mediaType.statusTypes.forEach {
            val icon = MediaType.MOVIES.getStatusIcon(it)

            IconButton(
                onClick = onChangeStatus
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = it.name
                )
            }
        }
    }
}

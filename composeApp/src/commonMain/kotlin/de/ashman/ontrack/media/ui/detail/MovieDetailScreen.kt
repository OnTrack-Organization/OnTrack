package de.ashman.ontrack.media.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import de.ashman.ontrack.media.domain.Movie
import de.ashman.ontrack.media.domain.StatusType
import de.ashman.ontrack.media.ui.StarRating
import de.ashman.ontrack.shelf.MediaType
import de.ashman.ontrack.shelf.ui.MovieViewModel
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    id: String,
    onBack: () -> Unit,
    viewModel: MovieViewModel = koinInject(),
) {
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
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Filled.Movie,
                        contentDescription = "Title Icon"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back Icon")
                    }
                },
            )
        }
    ) { innerPadding ->
        if (movie != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                PosterTitleAndInfo(
                    name = movie.name,
                    coverUrl = movie.coverUrl,
                    releaseDate = movie.releaseDate,
                    runtime = movie.runtime
                )

                ConsumeStatusRow(
                    mediaType = MediaType.MOVIES,
                    currentStatus = movie.consumeStatus,
                    onChangeStatus = onChangeStatus,
                )

                StarRating(
                    rating = movie.userRating,
                    onRatingChanged = onChangeRating
                )

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    },
                    content = { Icon(Icons.Filled.KeyboardArrowDown, "Arrow Down Icon") }
                )
            }
        } else {
            Text("This movie doesnt exist bruh")
        }
    }
}

@Composable
fun PosterTitleAndInfo(
    name: String,
    coverUrl: String?,
    releaseDate: String?,
    runtime: Int?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SubcomposeAsyncImage(
            model = coverUrl,
            contentDescription = "Cover"
        ) {
            val state = painter.state.collectAsState().value

            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator()
                }

                is AsyncImagePainter.State.Error -> {
                    Card(
                        modifier = Modifier.size(width = 200.dp, height = 300.dp)
                    ) {

                    }
                }

                else -> {
                    SubcomposeAsyncImageContent(
                        modifier = Modifier
                            .height(300.dp)
                            .shadow(elevation = 8.dp, RoundedCornerShape(12.dp))
                            .clip(shape = RoundedCornerShape(12.dp))
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        // TODO daten vorher verarbeiten evt
        val infoItems = listOfNotNull(
            releaseDate?.take(4),
            runtime?.let { "$it Min" }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            infoItems.forEachIndexed { index, item ->
                Text(
                    text = item,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (index < infoItems.size - 1) {
                    VerticalDivider(
                        color = Color.Gray,
                        thickness = 2.dp,
                        modifier = Modifier
                            .height(20.dp)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
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
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        mediaType.statusTypes.forEachIndexed { index, statusType ->
            val isSelected = currentStatus == statusType
            val icon = MediaType.MOVIES.getStatusIcon(statusType, isSelected)
            val label = stringResource(resource = statusTypeToStringRes(statusType))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    modifier = Modifier.scale(2f),
                    onClick = { onChangeStatus(statusType) },
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = statusType.name
                    )
                }
                Text(text = label, style = MaterialTheme.typography.bodySmall)
            }

            if (index < mediaType.statusTypes.size - 1) {
                Spacer(modifier = Modifier.width(64.dp))
            }
        }
    }
}

fun statusTypeToStringRes(statusType: StatusType): StringResource {
    return when (statusType) {
        StatusType.ALL -> Res.string.status_all
        StatusType.BINGING -> Res.string.status_binging
        StatusType.PLAYING -> Res.string.status_playing
        StatusType.READING -> Res.string.status_reading
        StatusType.WATCHED -> Res.string.status_watched
        StatusType.BINGED -> Res.string.status_binged
        StatusType.READ -> Res.string.status_read
        StatusType.PLAYED -> Res.string.status_played
        StatusType.DROPPED -> Res.string.status_dropped
        StatusType.CATALOG -> Res.string.status_catalog
    }
}
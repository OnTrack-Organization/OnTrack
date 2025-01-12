package de.ashman.ontrack.detail

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import de.ashman.ontrack.detail.content.AlbumDetailContent
import de.ashman.ontrack.detail.content.BoardgameDetailContent
import de.ashman.ontrack.detail.content.BookDetailContent
import de.ashman.ontrack.detail.content.MovieDetailContent
import de.ashman.ontrack.detail.content.ShowDetailContent
import de.ashman.ontrack.detail.content.VideogameDetailContent
import de.ashman.ontrack.media.model.Album
import de.ashman.ontrack.media.model.Boardgame
import de.ashman.ontrack.media.model.Book
import de.ashman.ontrack.media.model.ConsumeStatus
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.media.model.Movie
import de.ashman.ontrack.media.model.Show
import de.ashman.ontrack.media.model.Videogame
import de.ashman.ontrack.util.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.util.DEFAULT_POSTER_WIDTH
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    media: Media,
    viewModel: DetailViewModel,
) {
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(media.id) {
        viewModel.fetchDetails(media = media)
    }

    when (uiState.detailResultState) {
        DetailResultState.Loading -> {
            Column(
                modifier = modifier.fillMaxWidth().fillMaxHeight(0.5f).padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.scale(1.5f))
            }
        }

        DetailResultState.Success -> {
            if (uiState.selectedMedia != null) {
                DetailContent(
                    modifier = modifier.padding(16.dp),
                    media = uiState.selectedMedia,
                )
            }
        }

        DetailResultState.Error -> {
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f).padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(100.dp),
                    imageVector = Icons.Default.Error,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Error Icon"
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Network error",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    modifier: Modifier = Modifier,
    media: Media,
    onChangeStatus: (ConsumeStatus) -> Unit = {},
    onChangeRating: (Float) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        PosterTitleAndInfo(
            name = media.name,
            coverUrl = media.coverUrl,
            mainInfoItems = media.getMainInfoItems(),
        )

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            when (media.mediaType) {
                MediaType.MOVIE -> MovieDetailContent(movie = media as Movie)
                MediaType.SHOW -> ShowDetailContent(show = media as Show)
                MediaType.BOOK -> BookDetailContent(book = media as Book)
                MediaType.VIDEOGAME -> VideogameDetailContent(videogame = media as Videogame)
                MediaType.BOARDGAME -> BoardgameDetailContent(boardgame = media as Boardgame)
                MediaType.ALBUM -> AlbumDetailContent(album = media as Album)
            }
        }
        /*
                ConsumeStatusRow(
                    mediaType = media.type,
                    currentStatus = media.consumeStatus,
                    onChangeStatus = onChangeStatus,
                )

                StarRating(
                    rating = media.userRating,
                    onRatingChanged = onChangeRating
                )

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    },
                    content = { Icon(Icons.Filled.KeyboardArrowDown, "Arrow Down Icon") }
                )*/
    }
}

@Composable
fun PosterTitleAndInfo(
    modifier: Modifier = Modifier,
    name: String,
    coverUrl: String?,
    mainInfoItems: List<String>,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SubcomposeAsyncImage(
            model = coverUrl,
            contentScale = ContentScale.Crop,
            contentDescription = "Cover",
            modifier = modifier
                .size(width = DEFAULT_POSTER_WIDTH, height = DEFAULT_POSTER_HEIGHT)
                .clip(shape = RoundedCornerShape(16.dp))
        ) {
            val state = painter.state.collectAsState().value

            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    Card {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(modifier = Modifier.scale(1.5f))
                        }
                    }
                }

                is AsyncImagePainter.State.Error -> {
                    Card {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                modifier = Modifier.size(50.dp),
                                imageVector = Icons.Default.HideSource,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                contentDescription = "No Results Icon"
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "No cover found",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                else -> {
                    SubcomposeAsyncImageContent(
                        modifier = Modifier
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = if (name.isEmpty()) "No title found" else name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            mainInfoItems.forEachIndexed { index, item ->
                Text(
                    text = item,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (index < mainInfoItems.size - 1) {
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
    currentStatus: ConsumeStatus?,
    onChangeStatus: (ConsumeStatus) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        mediaType.consumeStatuses.forEachIndexed { index, statusType ->
            val isSelected = currentStatus == statusType
            val icon = statusType.getConsumeStatusIcon(isSelected)
            val label = stringResource(statusType.getConsumeStatusLabel())

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

            if (index < mediaType.consumeStatuses.size - 1) {
                Spacer(modifier = Modifier.width(64.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarRating(
    modifier: Modifier = Modifier,
    rating: Float?,
    onRatingChanged: (Float) -> Unit
) {
    var selectedRating by remember { mutableStateOf(rating ?: 0F) }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    val starWidth = 40.dp.toPx()
                    val draggedStars = (dragAmount / starWidth).coerceIn(-0.5f, 0.5f)
                    selectedRating = (selectedRating + draggedStars).coerceIn(0f, 5f)
                    onRatingChanged(selectedRating)
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        repeat(5) { index ->
            val starRating = (index + 1).toFloat()
            val animatedScale = remember { Animatable(1f) }
            val isHalfStar = (selectedRating - index) in 0.5f..1f

            LaunchedEffect(selectedRating) {
                val scale = if (selectedRating >= starRating) 1.2f else 1f
                animatedScale.animateTo(scale)
            }

            CompositionLocalProvider(
                LocalRippleConfiguration provides null
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    onClick = {
                        coroutineScope.launch {
                            animatedScale.animateTo(1.2f)
                            animatedScale.animateTo(1f)
                        }
                        selectedRating = starRating
                        onRatingChanged(selectedRating)
                    }
                ) {
                    if (selectedRating >= starRating) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFFFFC700)
                        )
                    } else if (isHalfStar) {
                        Icon(
                            Icons.AutoMirrored.Filled.StarHalf,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFFFFC700)
                        )
                    } else {
                        Icon(
                            Icons.Filled.StarBorder,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFFFFC700)
                        )
                    }
                }
            }
        }
    }
}

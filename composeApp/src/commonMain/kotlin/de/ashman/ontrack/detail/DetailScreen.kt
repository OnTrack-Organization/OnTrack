package de.ashman.ontrack.detail

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import de.ashman.ontrack.util.DEFAULT_POSTER_RATIO
import de.ashman.ontrack.util.SMALL_POSTER_HEIGHT
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.network_error
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    media: Media,
    viewModel: DetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(media.id) {
        viewModel.fetchDetails(media = media)
    }

    when (uiState.detailResultState) {
        DetailResultState.Loading -> {
            LoadingContent()
        }

        DetailResultState.Error -> {
            ErrorContent()
        }

        DetailResultState.Success -> {
            SuccessContent(
                media = uiState.selectedMedia,
            )
        }
    }
}

@Composable
fun SuccessContent(
    modifier: Modifier = Modifier,
    media: Media?,
    onChangeStatus: (ConsumeStatus) -> Unit = {},
    onChangeRating: (Float) -> Unit = {},
) {
    media?.let {
        val scrollState = rememberScrollState()

        val posterHeightRange = DEFAULT_POSTER_HEIGHT - SMALL_POSTER_HEIGHT
        val fractionScrolled = scrollState.value.toFloat() / scrollState.maxValue.toFloat().coerceAtLeast(1f)
        val targetPosterHeight = (DEFAULT_POSTER_HEIGHT - (posterHeightRange * fractionScrolled)).coerceAtLeast(SMALL_POSTER_HEIGHT)
        val targetPosterWidth = targetPosterHeight * DEFAULT_POSTER_RATIO

        val animatedPosterHeight by animateDpAsState(
            targetValue = targetPosterHeight.value.dp,
            animationSpec = tween(durationMillis = 100, easing = LinearEasing)
        )

        val animatedPosterWidth by animateDpAsState(
            targetValue = targetPosterWidth.value.dp,
            animationSpec = tween(durationMillis = 100, easing = LinearEasing)
        )

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            Box(
                modifier = Modifier
                    .height(animatedPosterHeight)
                    .width(animatedPosterWidth)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center,
            ) {
                MediaPoster(
                    coverUrl = media.coverUrl,
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            MediaTitle(
                title = media.name,
                textStyle = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )

            MainInfo(mainInfoItems = media.getMainInfoItems())

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(vertical = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = media.id,
                    style = MaterialTheme.typography.bodyLarge,
                )

                when (media.mediaType) {
                    MediaType.MOVIE -> MovieDetailContent(movie = media as Movie)
                    MediaType.SHOW -> ShowDetailContent(show = media as Show)
                    MediaType.BOOK -> BookDetailContent(book = media as Book)
                    MediaType.VIDEOGAME -> VideogameDetailContent(videogame = media as Videogame)
                    MediaType.BOARDGAME -> BoardgameDetailContent(boardgame = media as Boardgame)
                    MediaType.ALBUM -> AlbumDetailContent(album = media as Album)
                }
            }
        }
    }
}




@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().fillMaxHeight(0.5f).padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.scale(1.5f))
    }
}

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().fillMaxHeight(0.5f).padding(16.dp),
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
            text = stringResource(Res.string.network_error),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
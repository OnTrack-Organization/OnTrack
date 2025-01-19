package de.ashman.ontrack.features.detail

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.TrackStatus
import de.ashman.ontrack.domain.TrackStatusType
import de.ashman.ontrack.domain.Videogame
import de.ashman.ontrack.features.detail.ui.content.AlbumDetailContent
import de.ashman.ontrack.features.detail.ui.content.BoardgameDetailContent
import de.ashman.ontrack.features.detail.ui.content.BookDetailContent
import de.ashman.ontrack.features.detail.ui.content.MainInfo
import de.ashman.ontrack.features.detail.ui.content.MediaPoster
import de.ashman.ontrack.features.detail.ui.content.MediaTitle
import de.ashman.ontrack.features.detail.ui.content.MovieDetailContent
import de.ashman.ontrack.features.detail.ui.content.ShowDetailContent
import de.ashman.ontrack.features.detail.ui.content.VideogameDetailContent
import de.ashman.ontrack.features.track.TrackBottomSheetContent
import de.ashman.ontrack.features.track.getLabelForStatus
import de.ashman.ontrack.features.track.getStatusIcon
import de.ashman.ontrack.util.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.util.OnTrackButton
import de.ashman.ontrack.util.OnTrackIconButton
import de.ashman.ontrack.util.SMALL_POSTER_HEIGHT
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.network_error
import ontrack.composeapp.generated.resources.track_button
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    media: Media,
    viewModel: DetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(media.id) {
        viewModel.fetchDetails(media)
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
                onSaveTrackStatus = {
                    viewModel.saveTrack(it)
                },
                onRemoveTrack = {
                    viewModel.removeTrack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessContent(
    modifier: Modifier = Modifier,
    media: Media?,
    onSaveTrackStatus: (TrackStatus?) -> Unit,
    onRemoveTrack: () -> Unit,
) {
    media?.let {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var showBottomSheet by remember { mutableStateOf(false) }

        val listState = rememberLazyListState()
        val transition = updateTransition(listState.firstVisibleItemIndex != 0, label = "Image Size Transition")

        val size by transition.animateDp { isScrolling ->
            if (isScrolling) SMALL_POSTER_HEIGHT else DEFAULT_POSTER_HEIGHT
        }

        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            StickyMainContent(
                imageModifier = Modifier.height(size),
                media = media,
                trackStatus = media.trackStatus?.statusType,
                onClickTrack = { showBottomSheet = true },
                onClickRemove = onRemoveTrack,
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // TODO remove when we have more content
                item {
                }
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

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
            ) {
                TrackBottomSheetContent(
                    media = media,
                    onSaveTrackStatus = {
                        onSaveTrackStatus(it)
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun StickyMainContent(
    imageModifier: Modifier = Modifier,
    media: Media,
    trackStatus: TrackStatusType? = null,
    onClickTrack: () -> Unit,
    onClickRemove: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MediaPoster(
            modifier = imageModifier,
            coverUrl = media.coverUrl,
        )

        Column {
            MediaTitle(
                title = media.title,
                textStyle = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )

            MainInfo(mainInfoItems = media.getMainInfoItems())
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OnTrackButton(
                modifier = Modifier.weight(1f),
                text = if (trackStatus != null) getLabelForStatus(trackStatus, media.mediaType) else Res.string.track_button,
                icon = if (trackStatus != null) getStatusIcon(trackStatus, false) else Icons.Default.Add,
                onClick = onClickTrack,
            )
            if (trackStatus != null) {
                OnTrackIconButton(
                    icon = Icons.Default.Delete,
                    onClick = onClickRemove
                )
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

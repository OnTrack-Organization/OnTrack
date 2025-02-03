package de.ashman.ontrack.features.detail

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.Tracking
import de.ashman.ontrack.domain.Videogame
import de.ashman.ontrack.features.common.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.features.common.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.features.detail.components.ReviewCard
import de.ashman.ontrack.features.detail.components.StickyMainContent
import de.ashman.ontrack.features.detail.content.AlbumDetailContent
import de.ashman.ontrack.features.detail.content.BoardgameDetailContent
import de.ashman.ontrack.features.detail.content.BookDetailContent
import de.ashman.ontrack.features.detail.content.MovieDetailContent
import de.ashman.ontrack.features.detail.content.ShowDetailContent
import de.ashman.ontrack.features.detail.content.VideogameDetailContent
import de.ashman.ontrack.features.track.TrackingBottomSheetContent
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.network_error
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessContent(
    modifier: Modifier = Modifier,
    media: Media,
    tracking: Tracking?,
    onSaveTracking: (Tracking) -> Unit,
    onRemoveTracking: () -> Unit,
    onClickItem: (Media) -> Unit,
) {
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
            status = tracking?.status,
            onAddTracking = { showBottomSheet = true },
            onRemoveTracking = onRemoveTracking,
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // TODO fix scrolling bug
            item {
                Column() {
                    Text(media.id)
                    tracking?.let { Text(tracking.id) }
                }
            }

            item {
                //RatingCardRow()
            }

            tracking?.let {
                item {
                    ReviewCard(tracking = it)
                }
            }

            when (media.mediaType) {
                MediaType.MOVIE -> MovieDetailContent(movie = media as Movie, onClickItem = onClickItem)
                MediaType.SHOW -> ShowDetailContent(show = media as Show, onClickItem = onClickItem)
                MediaType.BOOK -> BookDetailContent(book = media as Book, onClickItem = onClickItem)
                MediaType.VIDEOGAME -> VideogameDetailContent(videogame = media as Videogame, onClickItem = onClickItem)
                MediaType.BOARDGAME -> BoardgameDetailContent(boardgame = media as Boardgame, onClickItem = onClickItem)
                MediaType.ALBUM -> AlbumDetailContent(album = media as Album, onClickItem = onClickItem)
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ) {
            TrackingBottomSheetContent(
                mediaId = media.id,
                mediaType = media.mediaType,
                mediaTitle = media.title,
                tracking = tracking,
                onSaveTracking = {
                    onSaveTracking(it)
                    showBottomSheet = false
                }
            )
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

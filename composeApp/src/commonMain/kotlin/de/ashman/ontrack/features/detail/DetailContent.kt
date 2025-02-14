package de.ashman.ontrack.features.detail

import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.api.getRatingType
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.Movie
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.Videogame
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.detail.components.RatingCardRow
import de.ashman.ontrack.features.detail.components.ReviewCard
import de.ashman.ontrack.features.detail.content.AlbumDetailContent
import de.ashman.ontrack.features.detail.content.BoardgameDetailContent
import de.ashman.ontrack.features.detail.content.BookDetailContent
import de.ashman.ontrack.features.detail.content.MovieDetailContent
import de.ashman.ontrack.features.detail.content.ShowDetailContent
import de.ashman.ontrack.features.detail.content.VideogameDetailContent
import de.ashman.ontrack.navigation.MediaNavigationItems
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_tracked_by
import ontrack.composeapp.generated.resources.ratings_overview
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    media: Media,
    friendTrackings: List<Tracking>,
    columnListState: LazyListState,
    onClickItem: (MediaNavigationItems) -> Unit,
    onUserClick: (String) -> Unit,
    onClickTrackingHistory: () -> Unit,
) {
    val rowListState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(rowListState))

    LazyColumn(
        state = columnListState,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (friendTrackings.isNotEmpty()) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(Res.string.detail_tracked_by),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        state = rowListState,
                        flingBehavior = flingBehavior,
                    ) {
                        items(friendTrackings) {
                            ReviewCard(
                                modifier = Modifier.fillParentMaxWidth(if (friendTrackings.size == 1) 1f else 0.95f),
                                tracking = it,
                                onClickTrackingHistory = onClickTrackingHistory,
                                onUserClick = { onUserClick(it.userId) },
                            )
                        }
                    }
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(Res.string.ratings_overview),
                    style = MaterialTheme.typography.titleMedium,
                )

                RatingCardRow(
                    apiType = media.mediaType.getRatingType(),
                    rating = media.apiRating,
                    ratingCount = media.apiRatingCount
                )
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
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

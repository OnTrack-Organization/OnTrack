package de.ashman.ontrack.features.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.api.getRatingType
import de.ashman.ontrack.domain.media.Album
import de.ashman.ontrack.domain.media.Boardgame
import de.ashman.ontrack.domain.media.Book
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.media.Movie
import de.ashman.ontrack.domain.media.Show
import de.ashman.ontrack.domain.media.Videogame
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.detail.components.FriendActivityRow
import de.ashman.ontrack.features.detail.components.RatingCardRow
import de.ashman.ontrack.features.detail.content.AlbumDetailContent
import de.ashman.ontrack.features.detail.content.BoardgameDetailContent
import de.ashman.ontrack.features.detail.content.BookDetailContent
import de.ashman.ontrack.features.detail.content.MovieDetailContent
import de.ashman.ontrack.features.detail.content.ShowDetailContent
import de.ashman.ontrack.features.detail.content.VideogameDetailContent
import de.ashman.ontrack.navigation.MediaNavigationItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    media: Media,
    friendTrackings: List<Tracking>,
    friendRecommendations: List<Recommendation>,
    columnListState: LazyListState,
    onClickItem: (MediaNavigationItems) -> Unit,
    onUserClick: (String) -> Unit,
    onShowFriendActivity: () -> Unit,
) {
    LazyColumn(
        state = columnListState,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (friendTrackings.isNotEmpty() || friendRecommendations.isNotEmpty()) {
            item {
                FriendActivityRow(
                    friendTrackings = friendTrackings,
                    friendRecommendations = friendRecommendations,
                    onUserClick = onUserClick,
                    onMoreClick = onShowFriendActivity
                )
            }
        }

        item {
            RatingCardRow(
                apiType = media.mediaType.getRatingType(),
                rating = media.apiRating,
                ratingCount = media.apiRatingCount
            )
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

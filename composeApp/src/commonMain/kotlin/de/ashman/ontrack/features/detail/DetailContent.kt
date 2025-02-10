package de.ashman.ontrack.features.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import de.ashman.ontrack.domain.Tracking
import de.ashman.ontrack.domain.Videogame
import de.ashman.ontrack.features.detail.components.RatingCardRow
import de.ashman.ontrack.features.detail.components.ReviewCard
import de.ashman.ontrack.features.detail.content.AlbumDetailContent
import de.ashman.ontrack.features.detail.content.BoardgameDetailContent
import de.ashman.ontrack.features.detail.content.BookDetailContent
import de.ashman.ontrack.features.detail.content.MovieDetailContent
import de.ashman.ontrack.features.detail.content.ShowDetailContent
import de.ashman.ontrack.features.detail.content.VideogameDetailContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    media: Media,
    tracking: Tracking?,
    searchDuration: Long,
    listState: LazyListState,
    onClickItem: (Media) -> Unit,
) {
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
                Text(
                    text = "Search took ${searchDuration}ms"
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

        tracking?.let {
            item {
                ReviewCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    tracking = it,
                )
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

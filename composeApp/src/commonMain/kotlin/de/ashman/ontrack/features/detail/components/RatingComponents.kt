package de.ashman.ontrack.features.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.api.utils.roundDecimals
import de.ashman.ontrack.domain.MediaType
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.bgg
import ontrack.composeapp.generated.resources.igdb
import ontrack.composeapp.generated.resources.on_track_icon_v1
import ontrack.composeapp.generated.resources.openlib
import ontrack.composeapp.generated.resources.spotify
import ontrack.composeapp.generated.resources.tmdb
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun RatingCardRow(
    ratingType: RatingType,
    rating: Double?,
    ratingCount: Int?,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RatingCard(
            modifier = Modifier.weight(1f),
            icon = RatingType.OnTrack.icon,
            rating = 0.0,
            ratingCount = 0,
            maxRating = RatingType.OnTrack.maxRating,
        )

        RatingCard(
            modifier = Modifier.weight(1f),
            icon = ratingType.icon,
            rating = rating,
            ratingCount = ratingCount,
            maxRating = ratingType.maxRating,
        )
    }
}

@Composable
fun RatingCard(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    rating: Double?,
    maxRating: Int?,
    ratingCount: Int?,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(MaterialTheme.shapes.small),
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                rating?.let {
                    Text(
                        text = "${rating.roundDecimals(1)} / $maxRating",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                ratingCount?.let {
                    Text(
                        text = "($ratingCount)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

enum class RatingType(
    val icon: DrawableResource,
    val maxRating: Int,
) {
    OnTrack(
        icon = Res.drawable.on_track_icon_v1,
        maxRating = 5,
    ),
    Movie(
        icon = Res.drawable.tmdb,
        maxRating = 10,
    ),
    Show(
        icon = Res.drawable.tmdb,
        maxRating = 10,
    ),
    Book(
        icon = Res.drawable.openlib,
        maxRating = 5,
    ),
    Album(
        icon = Res.drawable.spotify,
        maxRating = 100,
    ),
    Videogame(
        icon = Res.drawable.igdb,
        maxRating = 100,
    ),
    Boardgame(
        icon = Res.drawable.bgg,
        maxRating = 10,
    )
}

fun MediaType.getRatingType(): RatingType {
    return when (this) {
        MediaType.MOVIE -> return RatingType.Movie
        MediaType.SHOW -> return RatingType.Show
        MediaType.BOOK -> return RatingType.Book
        MediaType.ALBUM -> return RatingType.Album
        MediaType.VIDEOGAME -> return RatingType.Videogame
        MediaType.BOARDGAME -> return RatingType.Boardgame
    }
}
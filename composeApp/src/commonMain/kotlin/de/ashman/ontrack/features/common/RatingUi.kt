package de.ashman.ontrack.features.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.bgg
import ontrack.composeapp.generated.resources.igdb
import ontrack.composeapp.generated.resources.openlib
import ontrack.composeapp.generated.resources.spotify
import ontrack.composeapp.generated.resources.tmdb
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun RatingCardRow(
    onTrackRating: RatingUi,
    apiRating: RatingUi,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RatingCard(
            modifier = Modifier.weight(1f),
            icon = onTrackRating.icon,
            rating = onTrackRating.averageRating,
            maxRating = onTrackRating.maxRating,
            ratingCount = onTrackRating.totalRatings,
        )

        RatingCard(
            modifier = Modifier.weight(1f),
            icon = apiRating.icon,
            rating = apiRating.averageRating,
            maxRating = apiRating.maxRating,
            ratingCount = apiRating.totalRatings,
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(MaterialTheme.shapes.small),
            )

            Text("$rating / $maxRating ($ratingCount)")
        }
    }
}

data class RatingUi(
    val icon: DrawableResource,
    val maxRating: Int,
    val totalRatings: Int?,
    val averageRating: Double?,
) {
    companion object {
        fun OnTrack(averageRating: Double?, totalRatings: Int?) = RatingUi(
            icon = Res.drawable.spotify,
            totalRatings = totalRatings,
            averageRating = averageRating,
            maxRating = 5,
        )

        fun Movie(averageRating: Double?, totalRatings: Int?) = RatingUi(
            icon = Res.drawable.tmdb,
            totalRatings = totalRatings,
            averageRating = averageRating,
            maxRating = 10,
        )

        fun Show(averageRating: Double?, totalRatings: Int?) = RatingUi(
            icon = Res.drawable.tmdb,
            totalRatings = totalRatings,
            averageRating = averageRating,
            maxRating = 10,
        )

        fun Book(averageRating: Double?, totalRatings: Int?) = RatingUi(
            icon = Res.drawable.openlib,
            totalRatings = totalRatings,
            averageRating = averageRating,
            maxRating = 5,
        )

        fun Album(averageRating: Double?, totalRatings: Int?) = RatingUi(
            icon = Res.drawable.spotify,
            totalRatings = totalRatings,
            averageRating = averageRating,
            maxRating = 100,
        )

        fun Videogame(averageRating: Double?, totalRatings: Int?) = RatingUi(
            icon = Res.drawable.igdb,
            totalRatings = totalRatings,
            averageRating = averageRating,
            maxRating = 100,
        )

        fun Boardgame(averageRating: Double?, totalRatings: Int?) = RatingUi(
            icon = Res.drawable.bgg,
            totalRatings = totalRatings,
            averageRating = averageRating,
            maxRating = 10,
        )
    }
}

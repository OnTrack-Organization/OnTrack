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
import de.ashman.ontrack.api.ApiType
import de.ashman.ontrack.api.OnTrackApiType
import de.ashman.ontrack.api.utils.roundDecimals
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.ratings_overview
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RatingCardRow(
    apiType: ApiType,
    rating: Double?,
    ratingCount: Int?,
    appRating: Double?,
    appRatingCount: Int,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.ratings_overview),
            style = MaterialTheme.typography.titleMedium,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RatingCard(
                modifier = Modifier.weight(1f),
                icon = OnTrackApiType.OnTrack.icon,
                rating = appRating?.roundDecimals(1),
                ratingCount = appRatingCount,
                maxRating = OnTrackApiType.OnTrack.maxRating,
            )

            RatingCard(
                modifier = Modifier.weight(1f),
                icon = apiType.icon,
                rating = if (apiType == ApiType.Spotify) rating?.roundDecimals(0) else rating?.roundDecimals(1),
                ratingCount = ratingCount,
                maxRating = apiType.maxRating,
            )
        }
    }
}

@Composable
fun RatingCard(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    rating: String?,
    maxRating: Int?,
    ratingCount: Int?,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
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
                if (rating == null) {
                    Text(
                        text = "0 / $maxRating",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                } else {
                    Text(
                        text = "$rating / $maxRating",
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

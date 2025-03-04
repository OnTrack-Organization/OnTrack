package de.ashman.ontrack.features.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.api.ApiType
import de.ashman.ontrack.api.utils.roundDecimals
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.MAX_RATING
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.features.common.getIcon
import de.ashman.ontrack.features.common.getLabel
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
                icon = ApiType.OnTrack.icon,
                rating = "0.0",
                ratingCount = 0,
                maxRating = ApiType.OnTrack.maxRating,
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
                rating?.let {
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

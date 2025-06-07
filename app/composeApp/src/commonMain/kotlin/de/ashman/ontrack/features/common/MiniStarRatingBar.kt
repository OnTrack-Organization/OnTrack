package de.ashman.ontrack.features.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.ashman.ontrack.domain.tracking.MAX_RATING

@Composable
fun MiniStarRatingBar(
    modifier: Modifier = Modifier,
    rating: Double?,
    starColor: Color,
) {
    Row(
        modifier = modifier
    ) {
        val floorRating = rating?.toInt() ?: 0
        val hasHalfStar = rating != null && (rating - floorRating) >= 0.5

        for (i in 1..MAX_RATING) {
            val icon = when {
                i <= floorRating -> Icons.Filled.Star
                i == floorRating + 1 && hasHalfStar -> Icons.AutoMirrored.Default.StarHalf
                else -> Icons.Default.StarBorder
            }

            val tint = if (i <= floorRating || (i == floorRating + 1 && hasHalfStar)) {
                starColor
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
            )
        }
    }
}

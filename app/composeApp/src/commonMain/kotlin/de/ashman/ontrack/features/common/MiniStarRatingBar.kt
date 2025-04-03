package de.ashman.ontrack.features.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
        for (i in 1..MAX_RATING) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (rating != null && i <= rating) {
                    starColor
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

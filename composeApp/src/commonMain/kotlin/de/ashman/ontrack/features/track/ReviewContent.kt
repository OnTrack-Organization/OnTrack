package de.ashman.ontrack.features.track

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.util.OnTrackButton
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.review_title
import ontrack.composeapp.generated.resources.save_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReviewContent(
    mediaType: MediaType,
    mediaTitle: String,
    rating: Int?,
    review: String?,
    onRatingChange: (Int) -> Unit,
    onReviewChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.review_title, mediaTitle),
            style = MaterialTheme.typography.titleMedium,
        )

        StarRatingBar(
            rating = rating,
            onRatingChange = onRatingChange,
        )

        TextField(
            modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth(),
            value = review.orEmpty(),
            onValueChange = onReviewChange,
            placeholder = { Text(mediaType.getReviewLabel()) },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        OnTrackButton(
            text = Res.string.save_button,
            icon = Icons.Default.Save,
            onClick = onSave,
        )
    }
}

@Composable
fun StarRatingBar(
    rating: Int?,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (i in 1..maxRating) {
                Icon(
                    imageVector = if (rating != null && i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(62.dp)
                        .clickable(
                            onClick = { onRatingChange(i) },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        }
        Text(
            text = getRatingLabel(rating, maxRating),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}

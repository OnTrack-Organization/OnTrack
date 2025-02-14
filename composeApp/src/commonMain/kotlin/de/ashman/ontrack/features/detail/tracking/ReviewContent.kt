package de.ashman.ontrack.features.detail.tracking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.tracking.MAX_RATING
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackTextField
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.review_description_label
import ontrack.composeapp.generated.resources.review_title
import ontrack.composeapp.generated.resources.review_title_label
import ontrack.composeapp.generated.resources.save_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReviewContent(
    mediaTitle: String?,
    rating: Double?,
    reviewTitle: String?,
    reviewDescription: String?,
    onRatingChange: (Double) -> Unit,
    onReviewTitleChange: (String) -> Unit,
    onReviewDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    mediaTitle?.let {
        Text(
            text = stringResource(Res.string.review_title, it),
            style = MaterialTheme.typography.titleMedium,
        )
    }

    SelectableStarRatingBar(
        rating = rating,
        onRatingChange = onRatingChange,
    )

    OnTrackTextField(
        placeholder = stringResource(Res.string.review_title_label),
        value = reviewTitle,
        onValueChange = onReviewTitleChange,
    )

    OnTrackTextField(
        modifier = Modifier.height(100.dp),
        placeholder = stringResource(Res.string.review_description_label),
        value = reviewDescription,
        onValueChange = onReviewDescriptionChange,
    )

    OnTrackButton(
        text = Res.string.save_button,
        icon = Icons.Default.Save,
        onClick = onSave,
    )
}

@Composable
fun SelectableStarRatingBar(
    rating: Double?,
    onRatingChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
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
            for (i in 1..MAX_RATING) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (rating != null && i <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(
                            // TODO make this be able to have half stars
                            onClick = { onRatingChange(i.toDouble()) },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        }
        Text(
            text = getRatingLabel(rating, MAX_RATING),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}

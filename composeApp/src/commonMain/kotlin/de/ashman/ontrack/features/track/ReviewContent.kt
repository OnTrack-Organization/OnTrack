package de.ashman.ontrack.features.track

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import de.ashman.ontrack.domain.MAX_RATING
import de.ashman.ontrack.util.OnTrackButton
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.review_description_label
import ontrack.composeapp.generated.resources.review_title
import ontrack.composeapp.generated.resources.review_title_label
import ontrack.composeapp.generated.resources.save_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReviewContent(
    mediaTitle: String,
    rating: Int?,
    reviewTitle: String?,
    reviewDescription: String?,
    onRatingChange: (Int) -> Unit,
    onReviewTitleChange: (String) -> Unit,
    onReviewDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Text(
        text = stringResource(Res.string.review_title, mediaTitle),
        style = MaterialTheme.typography.titleMedium,
    )

    SelectableStarRatingBar(
        rating = rating,
        onRatingChange = onRatingChange,
    )

    TextField(
        modifier = Modifier.height(50.dp).fillMaxWidth(),
        value = reviewTitle.orEmpty(),
        onValueChange = onReviewTitleChange,
        placeholder = { Text(stringResource(Res.string.review_title_label)) },
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )

    TextField(
        modifier = Modifier.height(100.dp).fillMaxWidth(),
        value = reviewDescription.orEmpty(),
        onValueChange = onReviewDescriptionChange,
        placeholder = { Text(stringResource(Res.string.review_description_label)) },
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

@Composable
fun SelectableStarRatingBar(
    rating: Int?,
    onRatingChange: (Int) -> Unit,
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
            text = getRatingLabel(rating, MAX_RATING),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}

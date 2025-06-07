package de.ashman.ontrack.features.detail.review

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.tracking.MAX_RATING
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackTextField
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.features.common.getRatingLabel
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.review_description_label
import ontrack.composeapp.generated.resources.review_title
import ontrack.composeapp.generated.resources.review_title_label
import ontrack.composeapp.generated.resources.save_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReviewSheet(
    rating: Double?,
    title: String?,
    description: String?,
    trackStatus: TrackStatus?,
    isSaving: Boolean,
    onSave: (Double, String?, String?) -> Unit,
) {
    var rating by remember { mutableStateOf(rating) }
    var title by remember { mutableStateOf(title) }
    var description by remember { mutableStateOf(description) }

    Text(
        text = stringResource(Res.string.review_title),
        style = MaterialTheme.typography.titleMedium,
    )

    SelectableStarRatingBar(
        rating = rating,
        onRatingChange = { rating = it },
        trackStatus = trackStatus,
    )

    OnTrackTextField(
        placeholder = stringResource(Res.string.review_title_label),
        value = title,
        onValueChange = { title = it.takeIf { it.isNotBlank() } },
        singleLine = true,
    )

    OnTrackTextField(
        modifier = Modifier.height(100.dp),
        placeholder = stringResource(Res.string.review_description_label),
        value = description,
        onValueChange = { description = it.takeIf { it.isNotBlank() } },
    )

    OnTrackButton(
        modifier = Modifier.fillMaxWidth(),
        text = Res.string.save_button,
        icon = Icons.Default.Save,
        enabled = rating != null && !isSaving,
        isLoading = isSaving,
        onClick = {
            rating?.let {
                onSave(it, title, description)
            }
        },
    )
}

@Composable
fun SelectableStarRatingBar(
    rating: Double?,
    trackStatus: TrackStatus?,
    onRatingChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val starSize = 64.dp
    val contentColor = contentColorFor(trackStatus.getColor())

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
                val isFull = rating != null && i <= rating
                val isHalf = rating != null && (i - 1 < rating && rating < i)

                val scale by animateFloatAsState(
                    targetValue = if (isFull || isHalf) 1.2f else 1f,
                    label = "StarScale"
                )

                Box(
                    modifier = Modifier
                        .size(starSize)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val half = size.width / 2
                                val selected = if (offset.x < half) i - 0.5 else i.toDouble()
                                onRatingChange(selected)
                            }
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isFull) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (isHalf) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RectangleShape)
                                .drawWithContent {
                                    clipRect(right = size.width / 2) {
                                        this@drawWithContent.drawContent()
                                    }
                                }
                        )
                    }
                }
            }
        }

        AnimatedContent(
            targetState = rating,
            label = "RatingLabel"
        ) { targetState ->
            Text(
                text = getRatingLabel(targetState, MAX_RATING),
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

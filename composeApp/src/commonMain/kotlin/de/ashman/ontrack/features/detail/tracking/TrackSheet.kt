package de.ashman.ontrack.features.detail.tracking

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackOutlinedButton
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.features.common.getIcon
import de.ashman.ontrack.features.common.getLabel
import de.ashman.ontrack.features.common.getSublabel
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.review_button
import ontrack.composeapp.generated.resources.save_button
import ontrack.composeapp.generated.resources.track_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrackSheet(
    mediaType: MediaType,
    selectedStatus: TrackStatus?,
    onSelectStatus: (TrackStatus) -> Unit,
    onSave: () -> Unit,
    onToReview: () -> Unit,
) {
    Text(
        text = stringResource(Res.string.track_title),
        style = MaterialTheme.typography.titleMedium,
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TrackStatus.entries.forEach { status ->
            val isSelected = selectedStatus == status

            TrackStatusButton(
                onClick = { onSelectStatus(status) },
                icon = status.getIcon(isSelected),
                label = status.getLabel(mediaType),
                subLabel = status.getSublabel(mediaType),
                color = status.getColor(),
                isSelected = selectedStatus == status,
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OnTrackButton(
            modifier = Modifier.weight(1f).animateContentSize(),
            enabled = selectedStatus != null,
            text = Res.string.save_button,
            icon = Icons.Default.Save,
            onClick = onSave,
        )

        AnimatedVisibility(
            modifier = Modifier.weight(1f),
            visible = selectedStatus != null && selectedStatus != TrackStatus.CATALOG,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        ) {
            OnTrackOutlinedButton(
                text = Res.string.review_button,
                icon = Icons.Default.RateReview,
                onClick = onToReview,
            )
        }
    }
}

@Composable
fun TrackStatusButton(
    icon: ImageVector,
    label: StringResource,
    subLabel: StringResource,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedContainerColor by animateColorAsState(
        targetValue = color,
    )
    val animatedContentColor by animateColorAsState(
        targetValue = contentColorFor(color),
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(
            containerColor = if (isSelected) animatedContainerColor else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) animatedContentColor else MaterialTheme.colorScheme.onSurface,
        ),
        shape = MaterialTheme.shapes.medium,
        onClick = { onClick() },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedContent(
                targetState = icon,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
                }
            ) { targetIcon ->
                Icon(
                    modifier = Modifier.size(42.dp),
                    imageVector = targetIcon,
                    contentDescription = stringResource(label),
                    tint = if (isSelected) animatedContentColor else MaterialTheme.colorScheme.onSurface,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (isSelected) animatedContentColor else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(subLabel),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = if (isSelected) animatedContentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.size(42.dp))
        }
    }
}

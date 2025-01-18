package de.ashman.ontrack.features.track

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.sub.MediaType
import de.ashman.ontrack.domain.sub.TrackStatusType
import de.ashman.ontrack.util.OnTrackButton
import ontrack.composeapp.generated.resources.*
import ontrack.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrackBottomSheetContent(
    mediaType: MediaType,
    selectedTrackStatusType: TrackStatusType? = null,
    onSelectTrackStatusType: (TrackStatusType) -> Unit,
    onSaveTrackStatus: (TrackStatusType, String) -> Unit,
) {
    var currentContent by remember { mutableStateOf(TrackBottomSheetContent.TRACK_STATUS) }
    var reviewText by remember { mutableStateOf("") }

    when (currentContent) {
        TrackBottomSheetContent.TRACK_STATUS -> TrackStatusContent(
            mediaType = mediaType,
            selectedStatusType = selectedTrackStatusType,
            onSelectTrackStatusType = onSelectTrackStatusType,
            onContinue = { currentContent = TrackBottomSheetContent.REVIEW },
        )

        TrackBottomSheetContent.REVIEW -> ReviewContent(
            reviewText = reviewText,
            onReviewChange = { reviewText = it },
            onSave = {
                selectedTrackStatusType?.let { onSaveTrackStatus(it, reviewText) }
            },
        )
    }
}

@Composable
fun TrackStatusContent(
    mediaType: MediaType,
    selectedStatusType: TrackStatusType?,
    onSelectTrackStatusType: (TrackStatusType) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TrackStatusType.entries.forEach { status ->
            val isSelected = selectedStatusType == status

            TrackStatusButton(
                onClick = { onSelectTrackStatusType(status) },
                icon = getStatusIcon(status, isSelected),
                label = getLabelForStatus(status, mediaType),
                subLabel = getSublabelForStatus(mediaType, status),
                isSelected = selectedStatusType == status,
            )
        }

        OnTrackButton(
            text = Res.string.continue_button,
            onClick = onContinue,
        )
    }
}

@Composable
fun TrackStatusButton(
    onClick: () -> Unit,
    icon: ImageVector,
    label: StringResource,
    subLabel: StringResource,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        ),
        onClick = { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = icon,
                contentDescription = stringResource(label),
            )
            Column {
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(subLabel),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun ReviewContent(
    reviewText: String,
    onReviewChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextField(
            value = reviewText,
            onValueChange = onReviewChange,
            label = { Text(stringResource(Res.string.review_label)) },
        )
        OnTrackButton(
            text = Res.string.save_button,
            icon = Icons.Default.Save,
            onClick = onSave,
        )
    }
}

enum class TrackBottomSheetContent {
    TRACK_STATUS,
    REVIEW,
}
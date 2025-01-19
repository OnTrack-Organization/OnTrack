package de.ashman.ontrack.features.track

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.TrackStatusType
import de.ashman.ontrack.util.OnTrackButton
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.continue_button
import ontrack.composeapp.generated.resources.save_button
import ontrack.composeapp.generated.resources.track_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrackStatusContent(
    mediaType: MediaType,
    mediaTitle: String,
    selectedStatus: TrackStatusType?,
    onSelectStatus: (TrackStatusType) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(Res.string.track_title, mediaTitle),
        style = MaterialTheme.typography.titleMedium,
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TrackStatusType.entries.forEach { status ->
            val isSelected = selectedStatus == status

            TrackStatusButton(
                onClick = { onSelectStatus(status) },
                icon = getStatusIcon(status, isSelected),
                label = getLabelForStatus(status, mediaType),
                subLabel = getSublabelForStatus(mediaType, status),
                isSelected = selectedStatus == status,
            )
        }
    }

    OnTrackButton(
        text = if (selectedStatus == TrackStatusType.CATALOG) Res.string.save_button else Res.string.continue_button,
        icon = if (selectedStatus == TrackStatusType.CATALOG) Icons.Default.Save else Icons.AutoMirrored.Default.ArrowForward,
        onClick = onContinue,
    )
}

@Composable
fun TrackStatusButton(
    icon: ImageVector,
    label: StringResource,
    subLabel: StringResource,
    isSelected: Boolean,
    onClick: () -> Unit,
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
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(42.dp),
                imageVector = icon,
                contentDescription = stringResource(label),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(subLabel),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.size(42.dp))
        }
    }
}
package de.ashman.ontrack.features.track

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.sub.MediaType
import de.ashman.ontrack.domain.sub.TrackStatusType
import de.ashman.ontrack.util.OnTrackButton
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.continue_button
import ontrack.composeapp.generated.resources.save_button
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

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
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
            text = if (selectedStatusType == TrackStatusType.CATALOG) Res.string.save_button else Res.string.continue_button,
            icon = if (selectedStatusType == TrackStatusType.CATALOG) Icons.Default.Save else Icons.AutoMirrored.Default.ArrowForward,
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
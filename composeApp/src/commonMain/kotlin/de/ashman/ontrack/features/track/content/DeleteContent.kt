package de.ashman.ontrack.features.track.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackOutlinedButton
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.cancel_button
import ontrack.composeapp.generated.resources.delete_button
import ontrack.composeapp.generated.resources.delete_description
import ontrack.composeapp.generated.resources.delete_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteContent(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    Text(
        text = stringResource(Res.string.delete_title),
        style = MaterialTheme.typography.titleMedium,
    )

    Text(
        text = stringResource(Res.string.delete_description),
        style = MaterialTheme.typography.bodyMedium,
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OnTrackOutlinedButton(
            modifier = Modifier.weight(1f),
            text = Res.string.cancel_button,
            icon = Icons.Default.Close,
            onClick = onCancel,
        )
        OnTrackButton(
            modifier = Modifier.weight(1f),
            text = Res.string.delete_button,
            icon = Icons.Default.Delete,
            onClick = onDelete,
        )
    }
}
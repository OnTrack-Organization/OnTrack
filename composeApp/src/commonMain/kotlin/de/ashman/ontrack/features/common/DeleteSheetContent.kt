package de.ashman.ontrack.features.common

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
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.cancel_button
import ontrack.composeapp.generated.resources.delete_button
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteSheetContent(
    title: StringResource,
    text: StringResource,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Text(
        text = stringResource(title),
        style = MaterialTheme.typography.titleMedium,
    )

    Text(
        text = stringResource(text),
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
            onClick = onConfirm,
        )
    }
}
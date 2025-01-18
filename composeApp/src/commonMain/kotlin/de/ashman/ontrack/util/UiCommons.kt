package de.ashman.ontrack.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnTrackButton(
    modifier: Modifier = Modifier,
    text: StringResource,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.fillMaxWidth().height(48.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
        ),
    ) {
        icon?.let { Icon(modifier = Modifier.size(24.dp), imageVector = icon, contentDescription = icon.name) }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
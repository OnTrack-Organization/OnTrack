package de.ashman.ontrack.features.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
    isLoading: Boolean = false,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.fillMaxWidth().height(48.dp),
        onClick = onClick,
        enabled = !isLoading && enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 3.dp,
            )
        } else {
            icon?.let { Icon(modifier = Modifier.size(24.dp), imageVector = icon, contentDescription = icon.name) }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(text),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
fun OnTrackOutlinedButton(
    modifier: Modifier = Modifier,
    text: StringResource,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier.fillMaxWidth().height(48.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
    ) {
        icon?.let { Icon(modifier = Modifier.size(24.dp), imageVector = icon, contentDescription = icon.name) }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun OnTrackIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.size(48.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(0.dp),
        enabled = enabled,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = icon.name,
        )
    }
}

@Composable
fun OnTrackTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    value: String?,
    onValueChange: (String) -> Unit,
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value.orEmpty(),
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

// TODO maybe create my own
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnTrackCommentTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    value: String?,
    onValueChange: (String) -> Unit,
) {
   /* BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = value.orEmpty(),
        onValueChange = onValueChange,
        decorationBox = {
            TextFieldDefaults.DecorationBox(
                value = value.orEmpty(),
                visualTransformation = { value },
                innerTextField = it,
                placeholder = { Text(placeholder) },
                singleLine = true,
                enabled = true,
                interactionSource = MutableInteractionSource(),
                contentPadding = PaddingValues(0.dp),
            )
        }
    )*/
}
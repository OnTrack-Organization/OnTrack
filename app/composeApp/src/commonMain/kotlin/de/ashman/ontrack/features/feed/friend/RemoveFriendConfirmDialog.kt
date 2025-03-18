package de.ashman.ontrack.features.feed.friend

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.cancel_button
import ontrack.composeapp.generated.resources.feed_remove_friend_confirm_text
import ontrack.composeapp.generated.resources.feed_remove_friend_confirm_title
import ontrack.composeapp.generated.resources.remove_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun RemoveFriendConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(Res.string.feed_remove_friend_confirm_title)) },
        text = { Text(text = stringResource(Res.string.feed_remove_friend_confirm_text)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(Res.string.remove_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(Res.string.cancel_button))
            }
        },
    )
}
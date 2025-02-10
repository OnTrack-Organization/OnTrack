package de.ashman.ontrack.features.feed

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_delete_comment_cancel
import ontrack.composeapp.generated.resources.feed_delete_comment_confirm
import ontrack.composeapp.generated.resources.feed_delete_comment_description
import ontrack.composeapp.generated.resources.feed_delete_comment_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteCommentDialog(
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(Res.string.feed_delete_comment_title)) },
        text = { Text(text = stringResource(Res.string.feed_delete_comment_description)) },
        confirmButton = {
            TextButton(
                onClick = onConfirmDelete
            ) {
                Text(stringResource(Res.string.feed_delete_comment_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(Res.string.feed_delete_comment_cancel))
            }
        },
    )
}
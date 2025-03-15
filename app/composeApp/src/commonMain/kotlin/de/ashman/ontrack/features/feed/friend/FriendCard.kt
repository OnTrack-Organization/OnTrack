package de.ashman.ontrack.features.feed.friend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.GroupRemove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.features.common.PersonImage

@Composable
private fun FriendCard(
    imageUrl: String,
    username: String,
    name: String,
    onClickUser: () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickUser() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PersonImage(
                userImageUrl = imageUrl,
                onClick = onClickUser,
            )
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(content = buttons)
        }
    }
}

@Composable
fun PotentialFriendCard(
    imageUrl: String,
    username: String,
    name: String,
    isFriendRequestSent: Boolean,
    onClickUser: () -> Unit,
    onSendRequest: () -> Unit,
    onCancelRequest: () -> Unit,
) {
    FriendCard(
        imageUrl = imageUrl,
        username = username,
        name = name,
        onClickUser = onClickUser,
    ) {
        IconButton(onClick = if (isFriendRequestSent) onCancelRequest else onSendRequest) {
            Icon(
                imageVector = if (isFriendRequestSent) Icons.Rounded.Close else Icons.Rounded.GroupAdd,
                contentDescription = if (isFriendRequestSent) "Cancel Friend Request" else "Send Friend Request"
            )
        }
    }
}

@Composable
fun SentFriendRequestCard(
    imageUrl: String,
    username: String,
    name: String,
    onClickUser: () -> Unit,
    onCancelRequest: () -> Unit,
) {
    FriendCard(
        imageUrl = imageUrl,
        username = username,
        name = name,
        onClickUser = onClickUser,
    ) {
        IconButton(onClick = onCancelRequest) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Cancel Friend Request"
            )
        }
    }
}

@Composable
fun ReceivedFriendRequestCard(
    imageUrl: String,
    username: String,
    name: String,
    onClickUser: () -> Unit,
    onAcceptRequest: () -> Unit,
    onDenyRequest: () -> Unit,
) {
    FriendCard(
        imageUrl = imageUrl,
        username = username,
        name = name,
        onClickUser = onClickUser,
    ) {
        IconButton(onClick = onAcceptRequest) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Accept Friend Request"
            )
        }
        IconButton(onClick = onDenyRequest) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Decline Friend Request"
            )
        }
    }
}

@Composable
fun CurrentFriendCard(
    imageUrl: String,
    username: String,
    name: String,
    onClickUser: () -> Unit,
    onRemoveFriend: () -> Unit,
) {
    FriendCard(
        imageUrl = imageUrl,
        username = username,
        name = name,
        onClickUser = onClickUser,
    ) {
        IconButton(onClick = onRemoveFriend) {
            Icon(
                imageVector = Icons.Rounded.GroupRemove,
                contentDescription = "Remove Friend"
            )
        }
    }
}

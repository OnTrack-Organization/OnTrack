package de.ashman.ontrack.features.friend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import de.ashman.ontrack.domain.user.FriendStatus
import de.ashman.ontrack.domain.user.OtherUser
import de.ashman.ontrack.features.common.PersonImage

@Composable
fun FriendCard(
    friend: OtherUser,
    onClickUser: () -> Unit = {},
    onSendRequest: () -> Unit = {},
    onCancelRequest: () -> Unit = {},
    onAcceptRequest: () -> Unit = {},
    onDenyRequest: () -> Unit = {},
    onRemoveFriend: () -> Unit = {},
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
                profilePictureUrl = friend.user.profilePictureUrl,
                onClick = onClickUser,
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = friend.user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = friend.user.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row {
                when (friend.friendStatus) {
                    FriendStatus.REQUEST_SENT -> {
                        IconButton(onClick = onCancelRequest) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Cancel Friend Request"
                            )
                        }
                    }

                    FriendStatus.REQUEST_RECEIVED -> {
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

                    FriendStatus.FRIEND -> {
                        IconButton(onClick = onRemoveFriend) {
                            Icon(
                                imageVector = Icons.Rounded.GroupRemove,
                                contentDescription = "Remove Friend"
                            )
                        }
                    }

                    else -> {
                        IconButton(onClick = onSendRequest) {
                            Icon(
                                imageVector = Icons.Rounded.GroupAdd,
                                contentDescription = "Send Friend Request"
                            )
                        }
                    }
                }
            }
        }
    }
}

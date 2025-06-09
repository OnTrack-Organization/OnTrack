package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.PersonRemove
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.user.FriendStatus
import de.ashman.ontrack.features.common.OnTrackOutlinedButton
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.accept_request_button
import ontrack.composeapp.generated.resources.blocked_button
import ontrack.composeapp.generated.resources.cancel_request_button
import ontrack.composeapp.generated.resources.decline_request_button
import ontrack.composeapp.generated.resources.remove_friend_button
import ontrack.composeapp.generated.resources.send_request_button
import org.jetbrains.compose.resources.StringResource

data class FriendRequestButtonUiState(
    val text: StringResource,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun friendRequestButtonUiState(
    friendStatus: FriendStatus,
    onSendRequest: () -> Unit,
    onCancelRequest: () -> Unit,
    onRemoveFriend: () -> Unit,
): FriendRequestButtonUiState? {
    return when (friendStatus) {
        FriendStatus.STRANGER -> FriendRequestButtonUiState(
            text = Res.string.send_request_button,
            icon = Icons.Rounded.GroupAdd,
            onClick = onSendRequest
        )

        FriendStatus.REQUEST_SENT -> FriendRequestButtonUiState(
            text = Res.string.cancel_request_button,
            icon = Icons.Rounded.Close,
            onClick = onCancelRequest
        )

        FriendStatus.FRIEND -> FriendRequestButtonUiState(
            text = Res.string.remove_friend_button,
            icon = Icons.Rounded.PersonRemove,
            onClick = onRemoveFriend
        )

        else -> null
    }
}

@Composable
fun FriendRequestButton(
    friendStatus: FriendStatus,
    isBlocked: Boolean,
    onSendRequest: () -> Unit,
    onCancelRequest: () -> Unit,
    onRemoveFriend: () -> Unit,
    onAcceptRequest: () -> Unit,
    onDeclineRequest: () -> Unit,
) {
    if (isBlocked) {
        OnTrackOutlinedButton(
            text = Res.string.blocked_button,
            enabled = false,
            onClick = {},
        )
    } else {
        val buttonState = friendRequestButtonUiState(
            friendStatus = friendStatus,
            onSendRequest = onSendRequest,
            onCancelRequest = onCancelRequest,
            onRemoveFriend = onRemoveFriend,
        )

        if (friendStatus == FriendStatus.REQUEST_RECEIVED) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OnTrackOutlinedButton(
                    text = Res.string.accept_request_button,
                    icon = Icons.Rounded.Check,
                    onClick = onAcceptRequest
                )
                OnTrackOutlinedButton(
                    text = Res.string.decline_request_button,
                    icon = Icons.Rounded.Close,
                    onClick = onDeclineRequest
                )
            }
        } else {
            buttonState?.let {
                OnTrackOutlinedButton(
                    text = it.text,
                    icon = it.icon,
                    onClick = it.onClick
                )
            }
        }
    }
}

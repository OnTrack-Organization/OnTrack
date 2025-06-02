package de.ashman.ontrack.preview

import androidx.compose.runtime.Composable
import de.ashman.ontrack.domain.user.FriendStatus
import de.ashman.ontrack.features.friend.FriendCard

@OnTrackPreview
@Composable
fun FriendCardPreview() {
    FriendCard(
        friend = previewFriend,
    )
}

@OnTrackPreview
@Composable
fun StrangerCardPreview() {
    FriendCard(
        friend = previewFriend.copy(friendStatus = FriendStatus.STRANGER),
    )
}

@OnTrackPreview
@Composable
fun RequestSentCardPreview() {
    FriendCard(
        friend = previewFriend.copy(friendStatus = FriendStatus.REQUEST_SENT),
    )
}

@OnTrackPreview
@Composable
fun RequestReceivedCardPreview() {
    FriendCard(
        friend = previewFriend.copy(friendStatus = FriendStatus.REQUEST_RECEIVED),
    )
}
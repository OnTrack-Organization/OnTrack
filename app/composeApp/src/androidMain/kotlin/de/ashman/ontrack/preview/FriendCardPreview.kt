package de.ashman.ontrack.preview

import androidx.compose.runtime.Composable
import de.ashman.ontrack.domain.newdomains.FriendStatus
import de.ashman.ontrack.features.friend.FriendCard

@OnTrackPreview
@Composable
fun FriendCardPreview() {
    FriendCard(
        profilePictureUrl = previewUser.profilePictureUrl,
        name = previewUser.name,
        username = previewUser.username,
        friendStatus = FriendStatus.FRIEND,
    )
}

@OnTrackPreview
@Composable
fun StrangerCardPreview() {
    FriendCard(
        profilePictureUrl = previewUser.profilePictureUrl,
        name = previewUser.name,
        username = previewUser.username,
        friendStatus = FriendStatus.STRANGER,
    )
}

@OnTrackPreview
@Composable
fun RequestSentCardPreview() {
    FriendCard(
        profilePictureUrl = previewUser.profilePictureUrl,
        name = previewUser.name,
        username = previewUser.username,
        friendStatus = FriendStatus.REQUEST_SENT,
    )
}

@OnTrackPreview
@Composable
fun RequestReceivedCardPreview() {
    FriendCard(
        profilePictureUrl = previewUser.profilePictureUrl,
        name = previewUser.name,
        username = previewUser.username,
        friendStatus = FriendStatus.REQUEST_RECEIVED,
    )
}
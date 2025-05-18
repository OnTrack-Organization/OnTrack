package de.ashman.ontrack.preview

import androidx.compose.runtime.Composable
import de.ashman.ontrack.domain.user.FriendStatus
import de.ashman.ontrack.features.shelf.ShelfHeader
import de.ashman.ontrack.theme.OnTrackTheme

@OnTrackPreview
@Composable
fun ShelfHeaderPreview() {
    OnTrackTheme {
        ShelfHeader(
            name = previewUser.name,
            username = previewUser.username,
            profilePictureUrl = previewUser.profilePictureUrl,
            friendStatus = null,
        )
    }
}

@OnTrackPreview
@Composable
fun ShelfHeaderFriendPreview() {
    OnTrackTheme {
        ShelfHeader(
            name = previewUser.name,
            username = previewUser.username,
            profilePictureUrl = previewUser.profilePictureUrl,
            friendStatus = FriendStatus.FRIEND,
        )
    }
}

@OnTrackPreview
@Composable
fun ShelfHeaderStrangerPreview() {
    OnTrackTheme {
        ShelfHeader(
            name = previewUser.name,
            username = previewUser.username,
            profilePictureUrl = previewUser.profilePictureUrl,
            friendStatus = FriendStatus.STRANGER,
        )
    }
}

@OnTrackPreview
@Composable
fun ShelfHeaderRequestSentPreview() {
    OnTrackTheme {
        ShelfHeader(
            name = previewUser.name,
            username = previewUser.username,
            profilePictureUrl = previewUser.profilePictureUrl,
            friendStatus = FriendStatus.REQUEST_SENT,
        )
    }
}

@OnTrackPreview
@Composable
fun ShelfHeaderRequestReceivedPreview() {
    OnTrackTheme {
        ShelfHeader(
            name = previewUser.name,
            username = previewUser.username,
            profilePictureUrl = previewUser.profilePictureUrl,
            friendStatus = FriendStatus.REQUEST_RECEIVED,
        )
    }
}
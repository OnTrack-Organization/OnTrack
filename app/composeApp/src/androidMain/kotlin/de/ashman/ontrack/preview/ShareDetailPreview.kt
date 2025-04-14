package de.ashman.ontrack.preview

import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.share_detail.ShareDetailContent
import de.ashman.ontrack.theme.OnTrackTheme

@OnTrackPreview
@Composable
fun ShareDetailContentPreview() {
    OnTrackTheme {
        ShareDetailContent(
            tracking = previewTracking,
            onPostComment = {},
            onClickLike = {},
            onClickCover = {},
            onClickUser = {},
        )
    }
}

@OnTrackPreview
@Composable
fun ShareDetailContentEmptyPreview() {
    OnTrackTheme {
        ShareDetailContent(
            tracking = previewTracking.copy(
                likes = emptyList(),
                comments = emptyList(),
            ),
            onPostComment = {},
            onClickLike = {},
            onClickCover = {},
            onClickUser = {},
        )
    }
}
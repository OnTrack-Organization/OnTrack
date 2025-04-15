package de.ashman.ontrack.preview

import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.share_detail.ShareDetailContent
import de.ashman.ontrack.theme.OnTrackTheme

@OnTrackPreview
@Composable
fun ShareDetailContentSingleInteractionPreview() {
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
fun ShareDetailContentMultipleInteractionsPreview() {
    OnTrackTheme {
        ShareDetailContent(
            tracking = previewTracking.copy(
                likes = listOf(
                    previewLike,
                    previewLike,
                    previewLike,
                    previewLike,
                    previewLike,
                    previewLike,
                ),
                comments = listOf(
                    previewComment,
                    previewComment,
                    previewComment,
                    previewComment,
                    previewComment,
                    previewComment,
                ),
            ),
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
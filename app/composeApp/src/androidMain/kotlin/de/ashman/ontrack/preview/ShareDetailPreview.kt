package de.ashman.ontrack.preview

import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.share.ShareDetailContent
import de.ashman.ontrack.theme.OnTrackTheme

@OnTrackPreview
@Composable
fun ShareDetailContentSingleInteractionPreview() {
    OnTrackTheme {
        ShareDetailContent(
            post = previewPost,
        )
    }
}

@OnTrackPreview
@Composable
fun ShareDetailContentMultipleInteractionsPreview() {
    OnTrackTheme {
        ShareDetailContent(
            post = previewPost,
        )
    }
}

@OnTrackPreview
@Composable
fun ShareDetailContentEmptyPreview() {
    OnTrackTheme {
        ShareDetailContent(
            post = previewPost,
        )
    }
}
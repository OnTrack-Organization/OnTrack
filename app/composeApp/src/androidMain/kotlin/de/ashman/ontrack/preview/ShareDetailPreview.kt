package de.ashman.ontrack.preview

import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.share.PostDetailContent
import de.ashman.ontrack.theme.OnTrackTheme

@OnTrackPreview
@Composable
fun ShareDetailContentSingleInteractionPreview() {
    OnTrackTheme {
        PostDetailContent(
            post = previewPost,
        )
    }
}

@OnTrackPreview
@Composable
fun ShareDetailContentMultipleInteractionsPreview() {
    OnTrackTheme {
        PostDetailContent(
            post = previewPost,
        )
    }
}

@OnTrackPreview
@Composable
fun ShareDetailContentEmptyPreview() {
    OnTrackTheme {
        PostDetailContent(
            post = previewPost,
        )
    }
}
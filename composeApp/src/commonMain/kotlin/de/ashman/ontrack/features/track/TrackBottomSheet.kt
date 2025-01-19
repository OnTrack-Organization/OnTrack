package de.ashman.ontrack.features.track

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.ashman.ontrack.domain.sub.MediaType
import de.ashman.ontrack.domain.sub.TrackStatusType

@Composable
fun TrackBottomSheetContent(
    mediaTitle: String,
    mediaType: MediaType,
    currentTrackStatus: TrackStatusType?,
    currentRating: Int?,
    onSaveTrackStatus: (TrackStatusType, String?, Int?) -> Unit,
) {
    var currentContent by remember { mutableStateOf(CurrentTrackContent.TRACK_STATUS) }
    // TODO put into vm and check if set and do a confirm when closing the sheet
    var review by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(currentRating) }
    var selectedTrackStatus by remember { mutableStateOf<TrackStatusType?>(currentTrackStatus) }

    // TODO add back handling
    // Back Handling is being worked on rn
    // https://youtrack.jetbrains.com/issue/CMP-4419

    when (currentContent) {
        CurrentTrackContent.TRACK_STATUS -> TrackStatusContent(
            mediaType = mediaType,
            selectedStatusType = selectedTrackStatus,
            onSelectTrackStatusType = { selectedTrackStatus = it },
            onContinue = {
                if (selectedTrackStatus != TrackStatusType.CATALOG) {
                    currentContent = CurrentTrackContent.REVIEW
                } else {
                    selectedTrackStatus?.let { onSaveTrackStatus(it, null, null) }
                }
            },
        )

        CurrentTrackContent.REVIEW -> ReviewContent(
            mediaTitle = mediaTitle,
            mediaType = mediaType,
            review = review,
            rating = rating,
            onReviewChange = { review = it },
            onRatingChange = { rating = it },
            onSave = {
                selectedTrackStatus?.let { onSaveTrackStatus(it, review, rating) }
            },
        )
    }
}

enum class CurrentTrackContent {
    TRACK_STATUS,
    REVIEW,
}

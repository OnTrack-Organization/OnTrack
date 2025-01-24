package de.ashman.ontrack.features.track

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.TrackStatus
import de.ashman.ontrack.domain.TrackStatusType

// TODO add back handling
// Back Handling is being worked on rn
// https://youtrack.jetbrains.com/issue/CMP-4419

enum class CurrentTrackContent {
    TRACK_STATUS,
    REVIEW,
}

@Composable
fun TrackBottomSheetContent(
    media: Media,
    onSaveTrack: (TrackStatus?) -> Unit,
) {
    val localFocusManager = LocalFocusManager.current

    var currentContent by remember { mutableStateOf(CurrentTrackContent.TRACK_STATUS) }
    var trackStatus by remember { mutableStateOf(media.trackStatus ?: TrackStatus()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (currentContent) {
            CurrentTrackContent.TRACK_STATUS -> TrackStatusContent(
                mediaType = media.mediaType,
                mediaTitle = media.title,
                selectedStatus = trackStatus.statusType,
                onSelectStatus = { trackStatus = trackStatus.copy(statusType = it) },
                onContinue = {
                    if (trackStatus.statusType != TrackStatusType.CATALOG) {
                        currentContent = CurrentTrackContent.REVIEW
                    } else {
                        onSaveTrack(trackStatus)
                    }
                },
            )

            CurrentTrackContent.REVIEW -> ReviewContent(
                mediaTitle = media.title,
                reviewTitle = trackStatus.reviewTitle,
                reviewDescription = trackStatus.reviewDescription,
                rating = trackStatus.rating,
                onReviewTitleChange = { trackStatus = trackStatus.copy(reviewTitle = it) },
                onReviewDescriptionChange = { trackStatus = trackStatus.copy(reviewDescription = it) },
                onRatingChange = { trackStatus = trackStatus.copy(rating = it) },
                onSave = { onSaveTrack(trackStatus) },
            )
        }
    }
}

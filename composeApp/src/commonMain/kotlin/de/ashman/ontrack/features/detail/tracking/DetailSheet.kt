package de.ashman.ontrack.features.detail.tracking

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
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.DeleteSheetContent
import kotlinx.datetime.Clock.System
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_delete_confirm_text
import ontrack.composeapp.generated.resources.detail_delete_confirm_title

enum class CurrentBottomSheetContent {
    TRACKING,
    REVIEW,
    DELETE
}

// TODO add back handling
// Back Handling is being worked on rn
// https://youtrack.jetbrains.com/issue/CMP-4419
@Composable
fun DetailSheet(
    currentContent: CurrentBottomSheetContent,
    mediaId: String,
    mediaType: MediaType,
    mediaTitle: String?,
    mediaCoverUrl: String?,
    tracking: Tracking?,
    onSaveTracking: (Tracking) -> Unit,
    onDeleteTracking: (String) -> Unit,
    onCancel: () -> Unit,
    goToReview: () -> Unit,
) {
    val localFocusManager = LocalFocusManager.current
    // Copy previous tracking with new id and timestamp or create new tracking with filled media data
    var tracking by remember {
        mutableStateOf(
            tracking?.copy(
                timestamp = System.now().toEpochMilliseconds(),
            ) ?: Tracking(
                mediaId = mediaId,
                mediaType = mediaType,
                mediaTitle = mediaTitle,
                mediaCoverUrl = mediaCoverUrl,
            )
        )
    }

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
            CurrentBottomSheetContent.TRACKING -> TrackingContent(
                mediaType = mediaType,
                mediaTitle = mediaTitle,
                selectedStatus = tracking.status,
                onSelectStatus = { tracking = tracking.copy(status = it) },
                onSave = {
                    tracking = tracking.copy(
                        rating = if (tracking.status == TrackStatus.CATALOG) null else tracking.rating,
                        reviewTitle = if (tracking.status == TrackStatus.CATALOG) null else tracking.reviewTitle,
                        reviewDescription = if (tracking.status == TrackStatus.CATALOG) null else tracking.reviewDescription,
                        timestamp = System.now().toEpochMilliseconds()
                    ).also {
                        onSaveTracking(it)
                    }
                },
                onToReview = goToReview,
            )

            CurrentBottomSheetContent.REVIEW -> ReviewContent(
                mediaTitle = mediaTitle,
                reviewTitle = tracking.reviewTitle,
                reviewDescription = tracking.reviewDescription,
                rating = tracking.rating,
                trackStatus = tracking.status,
                onReviewTitleChange = { tracking = tracking.copy(reviewTitle = it) },
                onReviewDescriptionChange = { tracking = tracking.copy(reviewDescription = it) },
                onRatingChange = { tracking = tracking.copy(rating = it) },
                onSave = { onSaveTracking(tracking) },
            )

            CurrentBottomSheetContent.DELETE -> DeleteSheetContent(
                title = Res.string.detail_delete_confirm_title,
                text = Res.string.detail_delete_confirm_text,
                onConfirm = { onDeleteTracking(tracking.id) },
                onCancel = onCancel,
            )
        }
    }
}

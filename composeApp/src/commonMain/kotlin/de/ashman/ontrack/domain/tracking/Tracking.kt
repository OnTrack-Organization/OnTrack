package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@CommonParcelize
@Serializable
data class Tracking(
    @OptIn(ExperimentalUuidApi::class)
    val id: String = Uuid.random().toString(),

    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String? = null,
    val mediaCoverUrl: String? = null,

    val status: TrackStatus? = null,
    val rating: Double? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,

    val userId: String = Firebase.auth.currentUser?.uid.orEmpty(),
    val username: String = Firebase.auth.currentUser?.displayName.orEmpty(),
    val userImageUrl: String = Firebase.auth.currentUser?.photoURL.orEmpty(),

    val likedBy: List<TrackingLike> = listOf(),
    val comments: List<TrackingComment> = listOf(),
    val history: List<TrackingHistoryEntry> = listOf(),

    val timestamp: Long = System.now().toEpochMilliseconds(),
) : CommonParcelable {
    val likeCount: Int
        get() = likedBy.size

    val likeImages: List<String>
        get() = likedBy.map { it.userImageUrl }.take(3)

    val isLikedByCurrentUser: Boolean
        get() = likedBy.any { it.userId == Firebase.auth.currentUser?.uid }

    val commentCount: Int
        get() = comments.size
}

@Serializable
enum class TrackStatus {
    CATALOG, CONSUMING, CONSUMED, DROPPED
}

const val MAX_RATING = 5

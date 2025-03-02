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
    val mediaTitle: String,
    val mediaCoverUrl: String?,

    val status: TrackStatus? = null,
    val rating: Double? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,

    val userId: String = Firebase.auth.currentUser?.uid.orEmpty(),
    val username: String = Firebase.auth.currentUser?.displayName.orEmpty(),
    val userImageUrl: String = Firebase.auth.currentUser?.photoURL.orEmpty(),

    val likes: List<TrackingLike> = listOf(),
    val comments: List<TrackingComment> = listOf(),
    val history: List<TrackingHistoryEntry> = listOf(),

    val timestamp: Long = System.now().toEpochMilliseconds(),
) : CommonParcelable {
    val likeCount: Int
        get() = this@Tracking.likes.size

    val likeImages: List<String>
        get() = this@Tracking.likes.map { it.userImageUrl }.take(3)

    val isLikedByCurrentUser: Boolean
        get() = this@Tracking.likes.any { it.userId == Firebase.auth.currentUser?.uid }

    val commentCount: Int
        get() = comments.size
}

@Serializable
enum class TrackStatus {
    CATALOG, CONSUMING, CONSUMED, DROPPED
}

const val MAX_RATING = 5

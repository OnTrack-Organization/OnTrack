package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.db.entity.tracking.TrackingEntity
import de.ashman.ontrack.db.entity.user.UserData
import de.ashman.ontrack.domain.media.MediaType
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
    val userData: UserData,
    val updatedAt: Long = System.now().toEpochMilliseconds(),

    val mediaId: String,
    val mediaType: MediaType,
    val mediaTitle: String,
    val mediaCoverUrl: String,

    val status: TrackStatus,
    val rating: Double? = null,
    val reviewTitle: String? = null,
    val reviewDescription: String? = null,

    val likes: List<Like> = listOf(),
    val comments: List<Comment> = listOf(),
    val history: List<Entry> = listOf(),
) : CommonParcelable {
    val likeCount: Int
        get() = likes.size

    val isLikedByCurrentUser: Boolean
        get() = likes.any { it.userId == Firebase.auth.currentUser?.uid }

    val commentCount: Int
        get() = comments.size

    val likeImages: List<String>
        get() = likes.map { it.userImageUrl }.take(3)

    fun toEntity(): TrackingEntity = TrackingEntity(
        id = id,
        userData = userData,
        mediaId = mediaId,
        mediaType = mediaType,
        mediaTitle = mediaTitle,
        mediaCoverUrl = mediaCoverUrl,
        status = status,
        rating = rating,
        reviewTitle = reviewTitle,
        reviewDescription = reviewDescription,
        updatedAt = updatedAt,
    )
}

@Serializable
enum class TrackStatus {
    CATALOG, CONSUMING, CONSUMED, DROPPED
}

const val MAX_RATING = 5

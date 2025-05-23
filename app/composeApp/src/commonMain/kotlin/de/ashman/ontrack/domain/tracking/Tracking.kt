package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Deprecated("REMOVE WHEN FINALLY UNUSED")
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

    val userId: String,
    val username: String,
    val userImageUrl: String,

    val likes: List<Like> = listOf(),
    val comments: List<Comment> = listOf(),
    val history: List<Entry> = listOf(),

    val timestamp: Long,
) : CommonParcelable {
    val likeCount: Int
        get() = likes.size

    val likeImages: List<String>
        get() = likes.map { it.userImageUrl }.take(3)

    val isLikedByCurrentUser: Boolean
        get() = likes.any { it.userId == Firebase.auth.currentUser?.uid }

    val commentCount: Int
        get() = comments.size
}

const val MAX_RATING = 5

fun Tracking.isEquivalent(other: Tracking): Boolean {
    return this.status == other.status &&
            this.rating == other.rating &&
            this.reviewTitle == other.reviewTitle &&
            this.reviewDescription == other.reviewDescription
}

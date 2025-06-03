package de.ashman.ontrack.feature.user.repository

import de.ashman.ontrack.feature.friend.repository.FriendRepository
import de.ashman.ontrack.feature.friend.repository.FriendRequestRepository
import de.ashman.ontrack.feature.notification.repository.NotificationRepository
import de.ashman.ontrack.feature.recommendation.repository.RecommendationRepository
import de.ashman.ontrack.feature.review.repository.ReviewRepository
import de.ashman.ontrack.feature.share.repository.CommentRepository
import de.ashman.ontrack.feature.share.repository.LikeRepository
import de.ashman.ontrack.feature.share.repository.PostRepository
import de.ashman.ontrack.feature.tracking.repository.TrackingRepository
import de.ashman.ontrack.feature.user.domain.User
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository,
    private val trackingRepository: TrackingRepository,
    private val reviewRepository: ReviewRepository,
    private val recommendationRepository: RecommendationRepository,
    private val friendRepository: FriendRepository,
    private val friendRequestRepository: FriendRequestRepository,
    private val notificationRepository: NotificationRepository,
) {
    fun getById(id: String): User = userRepository.getReferenceById(id)

    fun findAllById(ids: List<String>): List<User> = userRepository.findAllById(ids)

    fun exists(id: String): Boolean = userRepository.existsById(id)

    fun findByEmail(email: String): User? = userRepository.findOneByEmail(email)

    fun searchByUsername(username: String, excludedId: String): List<User> =
        userRepository.findTop10ByUsernameContainingIgnoreCaseAndIdNotOrderByUsernameAsc(username, excludedId)

    fun save(user: User) = userRepository.save(user)

    // TODO fix this
    fun delete(id: String) {
        friendRequestRepository.deleteAllBySenderIdOrReceiverId(id, id)
        friendRepository.deleteAllByUser1IdOrUser2Id(id, id)

        notificationRepository.deleteAllBySenderIdOrReceiverId(id, id)

        recommendationRepository.deleteAllBySenderIdOrReceiverId(id, id)

        commentRepository.deleteAllByPostOwnerId(id)
        commentRepository.deleteAllByUserId(id)
        likeRepository.deleteAllByUserId(id)
        reviewRepository.deleteAllByUserId(id)
        trackingRepository.deleteAllByUserId(id)
        postRepository.deleteAllByUserId(id)

        userRepository.deleteById(id)
    }

}

package de.ashman.ontrack.feature.recommendation.service

import de.ashman.ontrack.feature.friend.repository.FriendRepository
import de.ashman.ontrack.feature.notification.service.NotificationService
import de.ashman.ontrack.feature.recommendation.controller.dto.*
import de.ashman.ontrack.feature.recommendation.domain.Recommendation
import de.ashman.ontrack.feature.recommendation.repository.RecommendationRepository
import de.ashman.ontrack.feature.review.controller.dto.toDto
import de.ashman.ontrack.feature.review.repository.ReviewRepository
import de.ashman.ontrack.feature.tracking.domain.MediaType
import de.ashman.ontrack.feature.tracking.domain.toEntity
import de.ashman.ontrack.feature.tracking.repository.TrackingRepository
import de.ashman.ontrack.feature.user.controller.dto.toDto
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val recommendationRepository: RecommendationRepository,
    private val trackingRepository: TrackingRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository,
    private val notificationService: NotificationService,
) {
    fun createRecommendation(senderId: String, dto: CreateRecommendationDto) {
        val sender = userRepository.getReferenceById(senderId)
        val receiver = userRepository.getReferenceById(dto.userId)

        val recommendation = Recommendation(
            sender = sender,
            receiver = receiver,
            media = dto.media.toEntity(),
            message = dto.message,
        )

        recommendationRepository.save(recommendation)
        notificationService.createRecommendationReceived(sender, receiver, recommendation)
    }

    fun getFriendsActivity(userId: String, mediaType: MediaType, mediaId: String): FriendsActivityDto {
        val friendIds = friendRepository.findFriendIdsOf(userId)
        val friends = userRepository.findAllById(friendIds)
        val friendsById = friends.associateBy { it.id }

        val recommendations = recommendationRepository
            .findByReceiverIdAndMediaIdAndMediaType(userId, mediaId, mediaType)

        val trackings = trackingRepository.findByUserIdInAndMediaIdAndMediaType(friendIds, mediaId, mediaType)

        val recommendationDtos = recommendations.mapNotNull { recommendation ->
            friendsById[recommendation.sender.id]?.toDto()?.let { userDto ->
                recommendation.toDto(userDto)
            }
        }

        val trackingDtos = trackings.mapNotNull { tracking ->
            val review = reviewRepository.getByTrackingId(tracking.id)
            val reviewDto = review?.toDto()

            friendsById[tracking.user.id]?.toDto()?.let { userDto ->
                tracking.toSimpleDto(userDto, reviewDto)
            }
        }

        return FriendsActivityDto(
            recommendations = recommendationDtos,
            trackings = trackingDtos,
        )
    }

    fun getSentRecommendations(senderId: String, receiverId: String, mediaType: MediaType, mediaId: String): List<RecommendationDto> {
        val sender = userRepository.getReferenceById(senderId)

        return recommendationRepository
            .findBySenderIdAndReceiverIdAndMediaIdAndMediaType(senderId, receiverId, mediaId, mediaType)
            .map { it.toDto(sender.toDto()) }
    }
}

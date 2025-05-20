package de.ashman.ontrack.recommendation.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.recommendation.controller.dto.*
import de.ashman.ontrack.recommendation.domain.Recommendation
import de.ashman.ontrack.recommendation.repository.RecommendationService
import de.ashman.ontrack.tracking.domain.model.MediaType
import de.ashman.ontrack.tracking.domain.model.toEntity
import de.ashman.ontrack.tracking.domain.repository.TrackingRepository
import de.ashman.ontrack.user.application.controller.user.toDto
import de.ashman.ontrack.user.domain.repository.FriendshipRepository
import de.ashman.ontrack.user.domain.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class RecommendationController(
    private val recommendationService: RecommendationService,
    private val trackingRepository: TrackingRepository,
    private val friendshipRepository: FriendshipRepository,
    private val userRepository: UserRepository,
) {
    @PostMapping("/recommend")
    fun createRecommendation(
        @RequestBody dto: CreateRecommendationDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Unit> {
        val recommendation = Recommendation(
            senderId = identity.id,
            receiverId = dto.userId,
            media = dto.media.toEntity(),
            message = dto.message,
        )

        recommendationService.save(recommendation)

        return ResponseEntity.ok().build()
    }

    @GetMapping("/friends-activity/{mediaType}/{mediaId}")
    fun getFriendsActivity(
        @PathVariable mediaType: MediaType,
        @PathVariable mediaId: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<FriendsActivityDto> {
        val friendIds = friendshipRepository.getFriendIds(identity.id)
        val friends = userRepository.findAllById(friendIds)
        val friendsById = friends.associateBy { it.id }

        val recommendations = recommendationService.findByReceiverAndMedia(
            receiverId = identity.id,
            mediaId = mediaId,
            mediaType = mediaType
        )

        val trackings = trackingRepository.getTrackingsByUserIdAndMedia(
            userIds = friendIds,
            mediaId = mediaId,
            mediaType = mediaType,
        )

        val recommendationDtos = recommendations.mapNotNull { recommendation ->
            friendsById[recommendation.senderId]?.toDto()?.let { userDto ->
                recommendation.toDto(userDto)
            }
        }

        val trackingDtos = trackings.mapNotNull { tracking ->
            friendsById[tracking.userId]?.toDto()?.let { userDto ->
                tracking.toSimpleDto(userDto)
            }
        }

        val friendsActivityDto = FriendsActivityDto(
            recommendations = recommendationDtos,
            trackings = trackingDtos,
        )

        return ResponseEntity.ok(friendsActivityDto)
    }

    // TODO maybe change so that we get all the sent recommendations, not just for a selected user
    @GetMapping("/sent-recommendations/{mediaType}/{mediaId}/{userId}")
    fun getSentRecommendations(
        @PathVariable mediaType: MediaType,
        @PathVariable mediaId: String,
        @PathVariable userId: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<RecommendationDto>> {
        val sender = userRepository.getById(identity.id)

        val recommendations = recommendationService.findBySenderAndReceiverAndMedia(
            senderId = identity.id,
            receiverId = userId,
            mediaId = mediaId,
            mediaType = mediaType
        )

        val recommendationDtos = recommendations.map { it.toDto(sender.toDto()) }

        return ResponseEntity.ok(recommendationDtos)
    }
}
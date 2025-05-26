package de.ashman.ontrack.feature.recommendation.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.friend.service.FriendService
import de.ashman.ontrack.feature.recommendation.controller.dto.*
import de.ashman.ontrack.feature.recommendation.domain.Recommendation
import de.ashman.ontrack.feature.recommendation.repository.RecommendationService
import de.ashman.ontrack.feature.review.controller.dto.toDto
import de.ashman.ontrack.feature.review.repository.ReviewService
import de.ashman.ontrack.feature.tracking.domain.MediaType
import de.ashman.ontrack.feature.tracking.domain.toEntity
import de.ashman.ontrack.feature.tracking.repository.TrackingService
import de.ashman.ontrack.feature.user.controller.dto.toDto
import de.ashman.ontrack.feature.user.repository.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class RecommendationController(
    private val recommendationService: RecommendationService,
    private val trackingService: TrackingService,
    private val reviewService: ReviewService,
    private val userService: UserService,
    private val friendService: FriendService,
) {
    @PostMapping("/recommend")
    fun createRecommendation(
        @RequestBody dto: CreateRecommendationDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Unit> {
        val sender = userService.getById(identity.id)
        val receiver = userService.getById(dto.userId)

        val recommendation = Recommendation(
            sender = sender,
            receiver = receiver,
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
        val friendIds = friendService.getFriendIds(identity.id)
        val friends = userService.findAllById(friendIds)
        val friendsById = friends.associateBy { it.id }

        val recommendations = recommendationService.findByReceiverAndMedia(
            receiverId = identity.id,
            mediaId = mediaId,
            mediaType = mediaType
        )

        val trackings = trackingService.getTrackingsByUserIdAndMedia(
            userIds = friendIds,
            mediaId = mediaId,
            mediaType = mediaType,
        )

        val recommendationDtos = recommendations.mapNotNull { recommendation ->
            friendsById[recommendation.sender.id]?.toDto()?.let { userDto ->
                recommendation.toDto(userDto)
            }
        }

        val trackingDtos = trackings.mapNotNull { tracking ->
            val review = reviewService.getByTrackingId(tracking.id)
            val reviewDto = review?.toDto()

            friendsById[tracking.user.id]?.toDto()?.let { userDto ->
                tracking.toSimpleDto(userDto, reviewDto)
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
        val sender = userService.getById(identity.id)

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
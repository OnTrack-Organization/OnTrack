package de.ashman.ontrack.feature.recommendation.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.recommendation.controller.dto.CreateRecommendationDto
import de.ashman.ontrack.feature.recommendation.controller.dto.FriendsActivityDto
import de.ashman.ontrack.feature.recommendation.controller.dto.RecommendationDto
import de.ashman.ontrack.feature.recommendation.service.RecommendationService
import de.ashman.ontrack.feature.tracking.domain.MediaType
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/recommendation")
class RecommendationController(
    private val recommendationService: RecommendationService,
) {
    @PostMapping("/recommend")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRecommendation(
        @RequestBody dto: CreateRecommendationDto,
        @AuthenticationPrincipal identity: Identity
    ) {
        recommendationService.createRecommendation(identity.id, dto)
    }

    @GetMapping("/friends-activity/{mediaType}/{mediaId}")
    @ResponseStatus(HttpStatus.OK)
    fun getFriendsActivity(
        @PathVariable mediaType: MediaType,
        @PathVariable mediaId: String,
        @AuthenticationPrincipal identity: Identity
    ): FriendsActivityDto {
        return recommendationService.getFriendsActivity(identity.id, mediaType, mediaId)
    }

    @GetMapping("/sent/{mediaType}/{mediaId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    fun getSentRecommendations(
        @PathVariable mediaType: MediaType,
        @PathVariable mediaId: String,
        @PathVariable userId: String,
        @AuthenticationPrincipal identity: Identity
    ): List<RecommendationDto> {
        return recommendationService.getSentRecommendations(identity.id, userId, mediaType, mediaId)
    }
}

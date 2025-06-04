package de.ashman.ontrack.feature.recommendation.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.recommendation.controller.dto.CreateRecommendationDto
import de.ashman.ontrack.feature.recommendation.controller.dto.FriendsActivityDto
import de.ashman.ontrack.feature.recommendation.controller.dto.RecommendationDto
import de.ashman.ontrack.feature.recommendation.service.RecommendationService
import de.ashman.ontrack.feature.tracking.domain.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/recommendation")
class RecommendationController(
    private val recommendationService: RecommendationService,
) {
    @PostMapping("/recommend")
    fun createRecommendation(
        @RequestBody dto: CreateRecommendationDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Unit> {
        recommendationService.createRecommendation(identity.id, dto)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/friends-activity/{mediaType}/{mediaId}")
    fun getFriendsActivity(
        @PathVariable mediaType: MediaType,
        @PathVariable mediaId: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<FriendsActivityDto> {
        val result = recommendationService.getFriendsActivity(identity.id, mediaType, mediaId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/sent/{mediaType}/{mediaId}/{userId}")
    fun getSentRecommendations(
        @PathVariable mediaType: MediaType,
        @PathVariable mediaId: String,
        @PathVariable userId: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<RecommendationDto>> {
        val result = recommendationService.getSentRecommendations(identity.id, userId, mediaType, mediaId)
        return ResponseEntity.ok(result)
    }
}

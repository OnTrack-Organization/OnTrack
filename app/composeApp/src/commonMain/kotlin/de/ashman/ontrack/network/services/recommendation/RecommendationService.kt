package de.ashman.ontrack.network.services.recommendation

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.recommendation.FriendsActivity
import de.ashman.ontrack.domain.recommendation.NewRecommendation
import de.ashman.ontrack.network.services.recommendation.dto.CreateRecommendationDto
import de.ashman.ontrack.network.services.recommendation.dto.FriendsActivityDto
import de.ashman.ontrack.network.services.recommendation.dto.RecommendationDto
import de.ashman.ontrack.network.services.recommendation.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

interface RecommendationService {
    suspend fun sendRecommendation(dto: CreateRecommendationDto): Result<Unit>
    suspend fun getFriendsActivity(mediaType: MediaType, mediaId: String): Result<FriendsActivity?>
    suspend fun getSentRecommendations(mediaType: MediaType, mediaId: String, userId: String): Result<List<NewRecommendation>>
}

class RecommendationServiceImpl(
    private val httpClient: HttpClient,
) : RecommendationService {
    override suspend fun sendRecommendation(dto: CreateRecommendationDto): Result<Unit> = safeApiCall {
        httpClient.post("/recommend") {
            setBody(dto)
        }
    }

    override suspend fun getFriendsActivity(mediaType: MediaType, mediaId: String): Result<FriendsActivity?> = safeApiCall {
        httpClient.get("/friends-activity/$mediaType/$mediaId").body<FriendsActivityDto>().toDomain()
    }

    override suspend fun getSentRecommendations(mediaType: MediaType, mediaId: String, userId: String): Result<List<NewRecommendation>> = safeApiCall {
        httpClient.get("/sent-recommendations/$mediaType/$mediaId/$userId").body<List<RecommendationDto>>().map { it.toDomain() }
    }
}

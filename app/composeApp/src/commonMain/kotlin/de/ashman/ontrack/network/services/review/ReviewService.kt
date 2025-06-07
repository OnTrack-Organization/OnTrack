package de.ashman.ontrack.network.services.review

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.review.Review
import de.ashman.ontrack.domain.review.ReviewStats
import de.ashman.ontrack.network.services.review.dto.CreateReviewDto
import de.ashman.ontrack.network.services.review.dto.ReviewDto
import de.ashman.ontrack.network.services.review.dto.ReviewStatsDto
import de.ashman.ontrack.network.services.review.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

interface ReviewService {
    suspend fun getReviews(): Result<List<Review>>
    suspend fun createReview(dto: CreateReviewDto): Result<Review>
    suspend fun updateReview(dto: CreateReviewDto): Result<Review>
    suspend fun getReviewStats(mediaType: MediaType, mediaId: String): Result<ReviewStats>
}

class ReviewServiceImpl(
    private val httpClient: HttpClient,
) : ReviewService {
    override suspend fun getReviews(): Result<List<Review>> = safeApiCall {
        httpClient.get("/review/all").body<List<ReviewDto>>().map { it.toDomain() }
    }

    override suspend fun createReview(dto: CreateReviewDto): Result<Review> = safeApiCall {
        httpClient.post("/review") {
            setBody(dto)
        }.body<ReviewDto>().toDomain()
    }

    override suspend fun updateReview(dto: CreateReviewDto): Result<Review> = safeApiCall {
        httpClient.put("/review") {
            setBody(dto)
        }.body<ReviewDto>().toDomain()
    }

    override suspend fun getReviewStats(mediaType: MediaType, mediaId: String): Result<ReviewStats> = safeApiCall {
        httpClient.get("/review/stats") {
            parameter("mediaType", mediaType)
            parameter("mediaId", mediaId)
        }.body<ReviewStatsDto>().toDomain()
    }
}

package de.ashman.ontrack.database.review

import de.ashman.ontrack.domain.review.Review
import de.ashman.ontrack.domain.review.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReviewRepository(
    private val reviewDao: ReviewDao,
) {
    fun getReview(trackingId: String?): Flow<Review?> = reviewDao.get(trackingId).map { it?.toDomain() }
    suspend fun addReview(review: Review) = reviewDao.add(review.toEntity())
    suspend fun addReviews(reviews: List<Review>) = reviewDao.add(reviews.map { it.toEntity() })
    suspend fun deleteAllReviews() = reviewDao.deleteAll()
}
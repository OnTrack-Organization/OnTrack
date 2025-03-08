package de.ashman.ontrack.db

import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.recommendation.RecommendationStatus
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface RecommendationRepository {
    suspend fun sendRecommendation(friendId: String, recommendation: Recommendation)
    suspend fun removeRecommendation(friendId: String, recommendationId: String)

    fun fetchRecommendations(mediaId: String): Flow<List<Recommendation>>

    suspend fun getPreviousSentRecommendations(friendId: String): List<Recommendation>

    suspend fun catalogRecommendation(mediaId: String)
    suspend fun passRecommendation(mediaId: String)
}

class RecommendationRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) : RecommendationRepository {

    private fun recommendationsCollection(userId: String) = firestore.collection("users").document(userId).collection("recommendations")

    override suspend fun sendRecommendation(friendId: String, recommendation: Recommendation) {
        recommendationsCollection(friendId)
            .document(recommendation.id)
            .set(recommendation)
    }

    override suspend fun removeRecommendation(friendId: String, recommendationId: String) {
        recommendationsCollection(friendId)
            .document(recommendationId)
            .delete()
    }

    override fun fetchRecommendations(mediaId: String): Flow<List<Recommendation>> {
        return recommendationsCollection(authRepository.currentUserId)
            .where { "mediaId" equalTo mediaId }
            .snapshots.map { snapshot ->
                snapshot.documents.map { it.data<Recommendation>() }
            }
    }

    override suspend fun getPreviousSentRecommendations(friendId: String): List<Recommendation> {
        val snapshot = recommendationsCollection(friendId)
            .where { "userId" equalTo authRepository.currentUserId }
            .get()

        return snapshot.documents
            .map { it.data<Recommendation>() }
            .sortedByDescending { it.timestamp }
    }

    // TODO maybe change...
    override suspend fun catalogRecommendation(mediaId: String) {
        val snapshot = recommendationsCollection(authRepository.currentUserId)
            .where { "mediaId" equalTo mediaId }
            .get()

        snapshot.documents.forEach { document ->
            document.reference.update("status" to RecommendationStatus.CATALOG)
        }
    }

    override suspend fun passRecommendation(mediaId: String) {
        val snapshot = recommendationsCollection(authRepository.currentUserId)
            .where { "mediaId" equalTo mediaId }
            .get()

        snapshot.documents.forEach { document ->
            document.reference.update("status" to RecommendationStatus.PASS)
        }
    }
}

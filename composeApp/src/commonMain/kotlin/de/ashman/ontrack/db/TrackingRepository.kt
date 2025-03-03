package de.ashman.ontrack.db

import co.touchlab.kermit.Logger
import de.ashman.ontrack.entity.tracking.EntryEntity
import de.ashman.ontrack.entity.tracking.TrackingEntity
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

interface TrackingRepository {
    suspend fun saveTracking(tracking: TrackingEntity)
    suspend fun removeTracking(trackingId: String)
    fun fetchTrackings(userId: String): Flow<List<TrackingEntity>>
    fun fetchTracking(trackingId: String): Flow<TrackingEntity?>
    suspend fun fetchFriendTrackings(mediaId: String): Flow<List<TrackingEntity>>
}

class TrackingRepositoryImpl(
    firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) : TrackingRepository {
    private val userCollection = firestore.collection("users")
    private fun userTrackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    override suspend fun saveTracking(tracking: TrackingEntity) {
        val trackingRef = userTrackingCollection(authRepository.currentUserId).document(tracking.id)

        // maybe get from viewmodel instead of here
        val existingTracking = if (trackingRef.get().exists) {
            trackingRef.get().data<TrackingEntity>()
        } else {
            null
        }

        val updatedHistory = existingTracking?.let {
            if (it.status != tracking.status && it.status != null) {
                it.history + EntryEntity(status = it.status, timestamp = it.timestamp)
            } else {
                it.history
            }
        } ?: emptyList()

        trackingRef.set(
            data = tracking.copy(history = updatedHistory),
            merge = true,
        )
    }

    override suspend fun removeTracking(trackingId: String) {
        userTrackingCollection(authRepository.currentUserId)
            .document(trackingId)
            .delete()
    }

    override fun fetchTracking(trackingId: String): Flow<TrackingEntity?> {
        return userTrackingCollection(authRepository.currentUserId)
            .document(trackingId)
            .snapshots
            .map { snapshot ->
                if (!snapshot.exists) {
                    Logger.i("Document does not exist for id: $trackingId")
                    null
                } else {
                    Logger.d("Document fetched for id: $trackingId")
                    snapshot.data<TrackingEntity>()
                }
            }
    }

    override fun fetchTrackings(userId: String): Flow<List<TrackingEntity>> {
        if (userId.isBlank()) {
            return flowOf(emptyList())
        }

        return userTrackingCollection(userId)
            .snapshots
            .mapNotNull { snapshot ->
                snapshot.documents.map { it.data<TrackingEntity>() }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun fetchFriendTrackings(mediaId: String): Flow<List<TrackingEntity>> {
        val currentUserId = authRepository.currentUserId

        val friendsFlow = userCollection.document(currentUserId)
            .collection("friends")
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.id } + currentUserId }

        return friendsFlow.flatMapLatest { friends ->
            val allFlows = friends.map { friendId ->
                userTrackingCollection(friendId)
                    .where { "mediaId" equalTo mediaId }
                    .snapshots()
                    .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>() } }
            }

            if (allFlows.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(allFlows) { trackingArrays ->
                    trackingArrays
                        .toList()
                        .flatten()
                        .groupBy { it.userId }
                        .mapValues { (_, trackings) -> trackings.maxByOrNull { it.timestamp } }
                        .values
                        .filterNotNull()
                        .sortedByDescending { it.timestamp }
                }
            }
        }
    }
}
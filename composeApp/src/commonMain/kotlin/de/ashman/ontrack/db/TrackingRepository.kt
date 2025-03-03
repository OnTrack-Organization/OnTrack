package de.ashman.ontrack.db

import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.toDomain
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.entity.toEntity
import de.ashman.ontrack.entity.toEntryEntity
import de.ashman.ontrack.entity.tracking.TrackingEntity
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface TrackingRepository {
    suspend fun saveTracking(tracking: Tracking)
    suspend fun removeTracking(trackingId: String)

    fun fetchTrackings(userId: String): Flow<List<Tracking>>
    fun fetchTracking(trackingId: String): Flow<Tracking?>

    suspend fun fetchFriendTrackingsForMedia(mediaId: String): Flow<List<Tracking>>
}

class TrackingRepositoryImpl(
    firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) : TrackingRepository {
    private val userCollection = firestore.collection("users")
    private fun trackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    override suspend fun saveTracking(tracking: Tracking) {
        val trackingRef = trackingCollection(authRepository.currentUserId).document(tracking.id)
        val snapshot = trackingRef.get()

        val existingTracking = snapshot.takeIf { it.exists }?.data<TrackingEntity>()
        val updatedHistory = existingTracking?.takeIf { it.status != tracking.status }
            ?.let { it.history + it.toEntryEntity() }
            ?: emptyList()

        trackingRef.set(
            tracking.toEntity().copy(history = updatedHistory),
            merge = true
        )
    }

    override suspend fun removeTracking(trackingId: String) {
        trackingCollection(authRepository.currentUserId)
            .document(trackingId)
            .delete()
    }

    override fun fetchTracking(trackingId: String): Flow<Tracking?> {
        return trackingCollection(authRepository.currentUserId)
            .document(trackingId)
            .snapshots
            .map { snapshot ->
                if (!snapshot.exists) {
                    Logger.i("Document does not exist for id: $trackingId")
                    null
                } else {
                    Logger.d("Document fetched for id: $trackingId")
                    snapshot.data<TrackingEntity>().toDomain()
                }
            }
    }

    override fun fetchTrackings(userId: String): Flow<List<Tracking>> {
        return trackingCollection(userId)
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>().toDomain() } }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun fetchFriendTrackingsForMedia(mediaId: String): Flow<List<Tracking>> {
        val currentUserId = authRepository.currentUserId

        return userCollection.document(currentUserId)
            .collection("friends")
            .snapshots
            .map { it.documents.map { doc -> doc.id } }
            .flatMapLatest { friends ->
                friends
                    .map { friendId ->
                        trackingCollection(friendId)
                            .where { "mediaId" equalTo mediaId }
                            .snapshots
                            .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>() } }
                    }
                    .let { flows ->
                        if (flows.isEmpty()) flowOf(emptyList())
                        else combine(flows) { trackingLists ->
                            trackingLists.toList().flatten()
                                .groupBy { it.userId }
                                .mapNotNull { (_, trackings) -> trackings.maxByOrNull { it.timestamp }?.toDomain() }
                                .sortedByDescending { it.timestamp }
                        }
                    }
            }
    }
}
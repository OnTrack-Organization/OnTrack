package de.ashman.ontrack.db

import co.touchlab.kermit.Logger
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.TrackingHistoryEntryEntity
import de.ashman.ontrack.db.entity.UserEntity
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

interface TrackingService {
    suspend fun saveTracking(tracking: TrackingEntity)
    suspend fun deleteTracking(trackingId: String)
    fun fetchTrackings(userId: String): Flow<List<TrackingEntity>>
    fun fetchTracking(trackingId: String): Flow<TrackingEntity?>
    suspend fun fetchFriendTrackings(mediaId: String): Flow<List<TrackingEntity>>
}

class TrackingServiceImpl(
    firestore: FirebaseFirestore,
    private val authService: AuthService,
) : TrackingService {
    private val userCollection = firestore.collection("users")
    private fun userTrackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    override suspend fun saveTracking(tracking: TrackingEntity) {
        val trackingRef = userTrackingCollection(authService.currentUserId).document(tracking.id)

        // maybe get from viewmodel instead of here
        val existingTracking = if (trackingRef.get().exists) {
            trackingRef.get().data<TrackingEntity>()
        } else {
            null
        }

        val updatedHistory = existingTracking?.let {
            if (it.status != tracking.status && it.status != null) {
                it.history + TrackingHistoryEntryEntity(status = it.status, timestamp = it.timestamp)
            } else {
                it.history
            }
        } ?: emptyList()

        trackingRef.set(
            data = tracking.copy(history = updatedHistory),
            merge = true,
        )
    }

    override suspend fun deleteTracking(trackingId: String) {
        userTrackingCollection(authService.currentUserId)
            .document(trackingId)
            .delete()
    }

    override fun fetchTracking(trackingId: String): Flow<TrackingEntity?> {
        return userTrackingCollection(authService.currentUserId)
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
        return userTrackingCollection(userId)
            .snapshots
            .mapNotNull { snapshot ->
                snapshot.documents.map { it.data<TrackingEntity>() }
            }
    }

    override suspend fun fetchFriendTrackings(mediaId: String): Flow<List<TrackingEntity>> {
        val currentUserId = authService.currentUserId

        val friends = userCollection.document(currentUserId).get().data<UserEntity>().friends.map { it.id } + currentUserId

        val allFlows = friends.map { friendId ->
            userTrackingCollection(friendId)
                .where { "mediaId" equalTo mediaId }
                .snapshots()
                .map { snapshot -> snapshot.documents.map { it.data<TrackingEntity>() } }
        }

        return if (allFlows.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(allFlows) { trackingLists ->
                trackingLists
                    .toList()
                    .flatten()
                    .distinctBy { it.id }
                    .sortedByDescending { it.timestamp }
            }
        }
    }
}
package de.ashman.ontrack.db

import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.TrackingHistoryEntryEntity
import de.ashman.ontrack.db.entity.toDomain
import de.ashman.ontrack.domain.tracking.Tracking
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface TrackingRepository {
    fun observeTrackings(userId: String): Flow<List<Tracking>>

    suspend fun fetchFriendTrackings(mediaId: String): Flow<List<TrackingEntity>>

    suspend fun saveTracking(tracking: TrackingEntity)
    suspend fun removeTracking(trackingId: String)
}

class TrackingRepositoryImpl(
    firestore: FirebaseFirestore,
    private val authService: AuthService,
) : TrackingRepository {
    private val userCollection = firestore.collection("users")
    private fun userTrackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    override fun observeTrackings(userId: String): Flow<List<Tracking>> {
        return userTrackingCollection(userId)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data<TrackingEntity>().toDomain() }
            }
            .distinctUntilChanged()
    }

    // TODO change of course
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun fetchFriendTrackings(mediaId: String): Flow<List<TrackingEntity>> {
        val currentUserId = authService.currentUserId

        val friendsFlow = userCollection.document(currentUserId)
            .collection("friends")
            .snapshots()
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

    override suspend fun removeTracking(trackingId: String) {
        userTrackingCollection(authService.currentUserId)
            .document(trackingId)
            .delete()
    }
}
package de.ashman.ontrack.db

import de.ashman.ontrack.db.entity.tracking.EntryEntity
import de.ashman.ontrack.db.entity.tracking.TrackingEntity
import de.ashman.ontrack.domain.tracking.Tracking
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

interface TrackingRepository {
    fun observeCurrentUserTrackings(): Flow<List<Tracking>>
    fun observeCurrentUserTracking(mediaId: String): Flow<Tracking?>

    suspend fun getUserTrackings(userId: String): List<Tracking>
    suspend fun saveTracking(tracking: Tracking)
    suspend fun removeTracking(trackingId: String)
}

class TrackingRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : TrackingRepository {
    private fun trackingsCollection(userId: String) = firestore.collection("users").document(userId).collection("trackings")
    private fun trackingEntriesCollection(userId: String, trackingId: String) = trackingsCollection(userId).document(trackingId).collection("entries")

    override fun observeCurrentUserTracking(mediaId: String): Flow<Tracking?> {
        return trackingsCollection(firebaseAuth.currentUser?.uid!!)
            .document(mediaId)
            .snapshots
            .map { snapshot ->
                if (!snapshot.exists) null else snapshot.data<Tracking>()
            }
    }

    override fun observeCurrentUserTrackings(): Flow<List<Tracking>> {
        return trackingsCollection(firebaseAuth.currentUser?.uid!!)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data<Tracking>() }
            }
    }

    override suspend fun getUserTrackings(userId: String): List<Tracking> {
        return trackingsCollection(userId)
            .get()
            .documents
            .map { it.data<Tracking>() }
    }


    override suspend fun saveTracking(tracking: Tracking) {
        val trackingRef = trackingsCollection(firebaseAuth.currentUser?.uid!!).document(tracking.id)
        val existingTracking = trackingRef.get()

        if (!existingTracking.exists) {
            // No existing tracking, create a new one
            trackingRef.set(tracking.copy(updatedAt = Clock.System.now().epochSeconds))
        } else {
            // Tracking exists, update it
            updateTracking(trackingRef, existingTracking.data<TrackingEntity>(), tracking.toEntity())
        }
    }

    private suspend fun updateTracking(
        trackingRef: DocumentReference,
        oldTracking: TrackingEntity,
        newTracking: TrackingEntity
    ) {
        if (oldTracking.status != newTracking.status) {
            val entry = EntryEntity(
                status = oldTracking.status,
                timestamp = Clock.System.now().epochSeconds
            )
            trackingEntriesCollection(firebaseAuth.currentUser?.uid!!, newTracking.id).add(entry)
        }

        val updatedTracking = newTracking.copy(updatedAt = Clock.System.now().epochSeconds)
        trackingRef.set(updatedTracking, merge = true)
    }

    override suspend fun removeTracking(trackingId: String) {
        trackingsCollection(firebaseAuth.currentUser?.uid!!)
            .document(trackingId)
            .delete()
    }

}
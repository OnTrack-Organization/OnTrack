package de.ashman.ontrack.database.tracking

import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.domain.tracking.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackingRepository(
    private val trackingDao: TrackingDao,
) {
    fun getTrackings(): Flow<List<Tracking>> = trackingDao.getAll().map { list -> list.map { it.toDomain() } }

    fun getTracking(mediaType: MediaType, mediaId: String): Flow<Tracking?> = trackingDao.get(mediaId, mediaType).map { it?.toDomain() }

    suspend fun addTrackings(trackings: List<Tracking>) = trackingDao.add(trackings.map { it.toEntity() })

    suspend fun addTracking(tracking: Tracking) = trackingDao.add(tracking.toEntity())

    suspend fun deleteTracking(id: String) = trackingDao.delete(id)

    suspend fun deleteAllTrackings() = trackingDao.deleteAll()
}
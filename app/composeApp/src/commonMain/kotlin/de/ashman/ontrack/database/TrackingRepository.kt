package de.ashman.ontrack.database

import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.newdomains.NewTracking
import de.ashman.ontrack.domain.newdomains.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackingRepository(
    private val trackingDao: TrackingDao,
) {
    fun getTrackings(): Flow<List<NewTracking>> = trackingDao.getTrackings().map { list -> list.map { it.toDomain() } }

    fun getTracking(mediaId: String, mediaType: MediaType): Flow<NewTracking?> = trackingDao.getTracking(mediaId, mediaType).map { it?.toDomain() }

    suspend fun addTrackings(trackings: List<NewTracking>) = trackingDao.addTrackings(trackings.map { it.toEntity() })

    suspend fun addTracking(tracking: NewTracking) = trackingDao.addTracking(tracking.toEntity())

    suspend fun deleteTracking(id: String) = trackingDao.deleteTracking(id)

    suspend fun deleteAllTrackings() = trackingDao.deleteAllTrackings()
}
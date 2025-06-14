package de.ashman.ontrack.network.services.tracking

import de.ashman.ontrack.api.utils.safeBackendApiCall
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.network.services.tracking.dto.CreateTrackingDto
import de.ashman.ontrack.network.services.tracking.dto.TrackingDto
import de.ashman.ontrack.network.services.tracking.dto.UpdateTrackingDto
import de.ashman.ontrack.network.services.tracking.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

interface TrackingService {
    suspend fun getTrackings(): Result<List<Tracking>>
    suspend fun createTracking(dto: CreateTrackingDto): Result<Tracking>
    suspend fun updateTracking(dto: UpdateTrackingDto): Result<Tracking>
    suspend fun deleteTracking(trackingId: String): Result<Unit>
}

class TrackingServiceImpl(
    private val httpClient: HttpClient,
) : TrackingService {
    override suspend fun getTrackings(): Result<List<Tracking>> = safeBackendApiCall<List<TrackingDto>> {
        httpClient.get("/tracking/all")
    }.mapCatching { response ->
        when (response.status) {
            HttpStatusCode.OK -> response.data.map { it.toDomain() }
            else -> error("Unexpected status: ${response.status}")
        }
    }

    override suspend fun createTracking(dto: CreateTrackingDto): Result<Tracking> = safeBackendApiCall<TrackingDto> {
        httpClient.post("/tracking") {
            setBody(dto)
        }
    }.mapCatching { response ->
        when (response.status) {
            HttpStatusCode.Created -> response.data.toDomain()
            else -> error("Unexpected status: ${response.status}")
        }
    }

    override suspend fun updateTracking(dto: UpdateTrackingDto): Result<Tracking> = safeBackendApiCall<TrackingDto> {
        httpClient.put("/tracking") {
            setBody(dto)
        }
    }.mapCatching { response ->
        when (response.status) {
            HttpStatusCode.OK -> response.data.toDomain()
            else -> error("Unexpected status: ${response.status}")
        }
    }

    override suspend fun deleteTracking(trackingId: String): Result<Unit> = safeBackendApiCall<Unit> {
        httpClient.delete("/tracking/$trackingId")
    }.mapCatching { response ->
        when (response.status) {
            HttpStatusCode.NoContent -> Unit
            else -> error("Unexpected status: ${response.status}")
        }
    }
}

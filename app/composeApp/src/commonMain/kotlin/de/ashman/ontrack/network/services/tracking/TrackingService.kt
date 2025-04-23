package de.ashman.ontrack.network.services.tracking

import de.ashman.ontrack.api.utils.safeBackendApiCall
import de.ashman.ontrack.domain.newdomains.NewTracking
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
    suspend fun createTracking(dto: CreateTrackingDto): Result<NewTracking>
    suspend fun updateTracking(dto: UpdateTrackingDto): Result<Unit>
    suspend fun deleteTracking(trackingId: String): Result<Unit>
    suspend fun getTrackings(): Result<List<NewTracking>>
}

class TrackingServiceImpl(
    private val httpClient: HttpClient,
) : TrackingService {
    override suspend fun createTracking(dto: CreateTrackingDto): Result<NewTracking> = safeBackendApiCall<TrackingDto> {
        httpClient.post("/tracking") {
            setBody(dto)
        }
    }.mapCatching { response ->
        when (response.status) {
            HttpStatusCode.OK -> response.data.toDomain()
            else -> error("Unexpected status: ${response.status}")
        }
    }

    override suspend fun updateTracking(dto: UpdateTrackingDto): Result<Unit> = safeBackendApiCall<Unit> {
        httpClient.put("/tracking") {
            setBody(dto)
        }
    }.mapCatching {
        when (it.status) {
            HttpStatusCode.OK -> Unit
            else -> error("Unexpected status: ${it.status}")
        }
    }

    override suspend fun deleteTracking(trackingId: String): Result<Unit> = safeBackendApiCall<Unit> {
        httpClient.delete("/tracking/$trackingId")
    }.mapCatching { response ->
        when (response.status) {
            HttpStatusCode.OK -> Unit
            else -> error("Unexpected status: ${response.status}")
        }
    }

    override suspend fun getTrackings(): Result<List<NewTracking>> = safeBackendApiCall<List<TrackingDto>> {
        httpClient.get("/trackings")
    }.mapCatching { response ->
        when (response.status) {
            HttpStatusCode.OK -> response.data.map { it.toDomain() }
            else -> error("Unexpected status: ${response.status}")
        }
    }
}

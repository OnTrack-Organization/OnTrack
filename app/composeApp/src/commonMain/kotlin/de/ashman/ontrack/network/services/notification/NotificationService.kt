package de.ashman.ontrack.network.services.notification

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.notification.Notification
import de.ashman.ontrack.network.services.notification.dto.NotificationDto
import de.ashman.ontrack.network.services.notification.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put

interface NotificationService {
    suspend fun getLatest(): Result<List<Notification>>
    suspend fun markAsRead(id: String): Result<Notification>
    suspend fun markAllAsRead(): Result<List<Notification>>
}

class NotificationServiceImpl(
    private val httpClient: HttpClient,
) : NotificationService {

    override suspend fun getLatest(): Result<List<Notification>> = safeApiCall {
        httpClient.get("/notifications").body<List<NotificationDto>>().map { it.toDomain() }
    }

    override suspend fun markAsRead(id: String): Result<Notification> = safeApiCall {
        httpClient.put("/notifications/$id/read").body<NotificationDto>().toDomain()
    }

    override suspend fun markAllAsRead(): Result<List<Notification>> = safeApiCall {
        httpClient.put("/notifications/read-all").body<List<NotificationDto>>().map { it.toDomain() }
    }
}

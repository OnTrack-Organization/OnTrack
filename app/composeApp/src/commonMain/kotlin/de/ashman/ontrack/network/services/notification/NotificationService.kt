package de.ashman.ontrack.network.services.notification

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.notification.Notification
import de.ashman.ontrack.network.services.notification.dto.NotificationDto
import de.ashman.ontrack.network.services.notification.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface NotificationService {
    suspend fun getLatestNotifications(): Result<List<Notification>>
}

class NotificationServiceImpl(
    private val httpClient: HttpClient,
) : NotificationService {

    override suspend fun getLatestNotifications(): Result<List<Notification>> = safeApiCall {
        httpClient.get("/notifications").body<List<NotificationDto>>().map { it.toDomain() }
    }
}

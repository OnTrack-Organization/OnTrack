package de.ashman.ontrack.feature.notification.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.notification.controller.dto.NotificationDto
import de.ashman.ontrack.feature.notification.controller.dto.toDto
import de.ashman.ontrack.feature.notification.service.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/notification")
class NotificationController(
    private val notificationService: NotificationService
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getLatestNotifications(
        @AuthenticationPrincipal identity: Identity
    ): List<NotificationDto> {
        val notifications = notificationService.getLatestNotificationsForUser(identity.id)
        val notificationDtos = notifications.map { it.toDto() }

        return notificationDtos
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    fun markAsRead(
        @PathVariable id: UUID,
        @AuthenticationPrincipal identity: Identity
    ): NotificationDto {
        return notificationService.markAsRead(id, identity.id).toDto()
    }

    @PutMapping("/read-all")
    @ResponseStatus(HttpStatus.OK)
    fun markAllAsRead(
        @AuthenticationPrincipal identity: Identity
    ): List<NotificationDto> {
        return notificationService.markAllAsRead(identity.id).map { it.toDto() }
    }
}

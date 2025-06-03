package de.ashman.ontrack.feature.notification.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.notification.controller.dto.NotificationDto
import de.ashman.ontrack.feature.notification.controller.dto.toDto
import de.ashman.ontrack.feature.notification.service.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {
    @GetMapping
    fun getLatestNotifications(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<NotificationDto>> {
        val notifications = notificationService.getLatestNotificationsForUser(identity.id)
        val notificationDtos = notifications.map { it.toDto() }

        return ResponseEntity.ok(notificationDtos)
    }

    @PutMapping("/{id}/read")
    fun markAsRead(
        @PathVariable id: UUID,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<NotificationDto> {
        val notificationDto = notificationService.markAsRead(id, identity.id).toDto()

        return ResponseEntity.ok(notificationDto)
    }

    @PutMapping("/read-all")
    fun markAllAsRead(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<NotificationDto>> {
        val notificationDtos = notificationService.markAllAsRead(identity.id).map { it.toDto() }

        return ResponseEntity.ok(notificationDtos)
    }
}

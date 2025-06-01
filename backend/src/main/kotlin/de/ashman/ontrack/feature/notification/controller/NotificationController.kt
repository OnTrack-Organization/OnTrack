package de.ashman.ontrack.feature.notification.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.notification.controller.dto.NotificationDto
import de.ashman.ontrack.feature.notification.controller.dto.toDto
import de.ashman.ontrack.feature.notification.service.NotificationService
import de.ashman.ontrack.feature.share.controller.dto.PostDto
import de.ashman.ontrack.feature.share.service.PostService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
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
    ): List<NotificationDto> {
        val notifications = notificationService.getLatestNotificationsForUser(identity.id)
        return notifications.map { it.toDto() }
    }

    @PostMapping("/{id}/read")
    fun markAsRead(
        @PathVariable id: UUID,
        @AuthenticationPrincipal identity: Identity
    ) {
        notificationService.markNotificationAsRead(userId = identity.id, notificationId = id)
    }
}

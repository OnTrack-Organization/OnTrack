package de.ashman.ontrack.feature.friend.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.friend.domain.FriendRequest
import de.ashman.ontrack.feature.friend.service.FriendRequestService
import de.ashman.ontrack.feature.friend.service.FriendService
import de.ashman.ontrack.feature.notification.service.NotificationService
import de.ashman.ontrack.feature.user.repository.UserService
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/friend-request")
class FriendRequestController(
    private val userService: UserService,
    private val friendRequestService: FriendRequestService,
    private val friendService: FriendService,
    private val notificationService: NotificationService,
) {
    @PostMapping("/send/{userId}")
    @Transactional
    fun sendFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ): ResponseEntity<Unit> {
        if (!userService.exists(userId)) {
            return ResponseEntity.notFound().build()
        }

        val sender = userService.getById(identity.id)
        val receiver = userService.getById(userId)

        val friendRequest = FriendRequest(sender = sender, receiver = receiver)

        friendRequestService.save(friendRequest)

        notificationService.createFriendRequestReceived(sender, receiver)

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/accept/{userId}")
    @Transactional
    fun acceptFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ): ResponseEntity<Unit> {
        val sender = userService.getById(userId)
        val receiver = userService.getById(identity.id)

        val request = friendRequestService.findBySenderAndReceiver(sender, receiver)
            ?: return ResponseEntity.notFound().build()

        request.accept()

        friendService.beginFriendship(sender, receiver)

        notificationService.createFriendRequestAccepted(acceptor = sender, originalSender = receiver)

        return ResponseEntity.ok().build()
    }

    @PostMapping("/decline/{userId}")
    @Transactional
    fun declineFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ): ResponseEntity<Unit> {
        val sender = userService.getById(userId)
        val receiver = userService.getById(identity.id)

        val request = friendRequestService.findBySenderAndReceiver(sender, receiver)
            ?: return ResponseEntity.notFound().build()

        request.decline()

        return ResponseEntity.ok().build()
    }

    @PostMapping("/cancel/{userId}")
    @Transactional
    fun cancelFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ): ResponseEntity<Unit> {
        val sender = userService.getById(identity.id)
        val receiver = userService.getById(userId)

        val request = friendRequestService.findBySenderAndReceiver(sender, receiver)
            ?: return ResponseEntity.notFound().build()

        request.cancel()

        return ResponseEntity.ok().build()
    }
}

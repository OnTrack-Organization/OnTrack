package de.ashman.ontrack.feature.friend.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.friend.domain.FriendRequest
import de.ashman.ontrack.feature.friend.repository.FriendRequestService
import de.ashman.ontrack.feature.friend.repository.FriendshipService
import de.ashman.ontrack.feature.user.repository.UserService
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FriendRequestController(
    private val userService: UserService,
    private val friendRequestService: FriendRequestService,
    private val friendshipService: FriendshipService
) {
    @PostMapping("friend-request/send/{userId}")
    @Transactional
    fun sendFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable("userId") userId: String
    ): ResponseEntity<Unit> {
        if (!userService.exists(userId)) {
            return ResponseEntity.notFound().build()
        }

        val friendRequest = FriendRequest(identity.id, userId)

        friendRequestService.save(friendRequest)

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("friend-request/accept/{userId}")
    @Transactional
    fun acceptFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable("userId") userId: String
    ): ResponseEntity<Unit> {
        val request = friendRequestService.findBySenderAndReceiver(userId, identity.id)

        if (request === null) {
            return ResponseEntity.notFound().build()
        }

        request.accept()

        friendshipService.beginFriendship(userId, identity.id)

        return ResponseEntity.ok().build()
    }

    @PostMapping("friend-request/decline/{userId}")
    @Transactional
    fun declineFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable("userId") userId: String
    ): ResponseEntity<Unit> {
        val request = friendRequestService.findBySenderAndReceiver(userId, identity.id)

        if (request === null) {
            return ResponseEntity.notFound().build()
        }

        request.decline()

        return ResponseEntity.ok().build()
    }

    @PostMapping("friend-request/cancel/{userId}")
    @Transactional
    fun cancelFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable("userId") userId: String
    ): ResponseEntity<Unit> {
        val request = friendRequestService.findBySenderAndReceiver(identity.id, userId)

        if (request === null) {
            return ResponseEntity.notFound().build()
        }

        request.cancel()

        return ResponseEntity.ok().build()
    }
}

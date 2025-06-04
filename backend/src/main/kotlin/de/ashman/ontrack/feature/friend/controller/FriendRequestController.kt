package de.ashman.ontrack.feature.friend.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.friend.service.FriendRequestService
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
    private val friendRequestService: FriendRequestService
) {
    @PostMapping("/send/{userId}")
    @Transactional
    fun sendFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ): ResponseEntity<Unit> {
        friendRequestService.sendRequest(identity.id, userId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/accept/{userId}")
    @Transactional
    fun acceptFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ): ResponseEntity<Unit> {
        friendRequestService.acceptRequest(identity.id, userId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/decline/{userId}")
    @Transactional
    fun declineFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ): ResponseEntity<Unit> {
        friendRequestService.declineRequest(identity.id, userId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/cancel/{userId}")
    @Transactional
    fun cancelFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ): ResponseEntity<Unit> {
        friendRequestService.cancelRequest(identity.id, userId)
        return ResponseEntity.ok().build()
    }
}

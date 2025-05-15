package de.ashman.ontrack.user.application.controller.friendrequest

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.user.domain.model.FriendRequest
import de.ashman.ontrack.user.domain.repository.FriendRequestRepository
import de.ashman.ontrack.user.domain.repository.FriendshipRepository
import de.ashman.ontrack.user.domain.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FriendRequestController(
    private val friendRequestRepository: FriendRequestRepository,
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository
) {
    @PostMapping("friend-request/send/{userId}")
    @Transactional
    fun sendFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable("userId") userId: String
    ): ResponseEntity<Unit> {
        if (!userRepository.exists(userId)) {
            return ResponseEntity.notFound().build()
        }

        val friendRequest = FriendRequest(identity.id, userId)
        friendRequestRepository.save(friendRequest)

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("friend-request/accept/{userId}")
    @Transactional
    fun acceptFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable("userId") userId: String
    ): ResponseEntity<Unit> {
        val request = friendRequestRepository.findBySenderAndReceiver(userId, identity.id)
        if (request === null) {
            return ResponseEntity.notFound().build()
        }

        request.accept()
        friendshipRepository.beginFriendship(userId, identity.id)

        return ResponseEntity.ok().build()
    }

    @PostMapping("friend-request/decline/{userId}")
    @Transactional
    fun declineFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable("userId") userId: String
    ): ResponseEntity<Unit> {
        val request = friendRequestRepository.findBySenderAndReceiver(userId, identity.id)
        if (request === null) {
            return ResponseEntity.notFound().build()
        }

        request.decline()

        return ResponseEntity.ok().build()
    }

    @PostMapping("friend-request/cancel/{userId}")
    @Transactional
    fun cancelFriendReqeust(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable("userId") userId: String
    ): ResponseEntity<Unit> {
        val request = friendRequestRepository.findBySenderAndReceiver(identity.id, userId)
        if (request === null) {
            return ResponseEntity.notFound().build()
        }

        request.cancel()

        return ResponseEntity.ok().build()
    }
}

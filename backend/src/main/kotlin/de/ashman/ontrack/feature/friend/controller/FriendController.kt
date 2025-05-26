package de.ashman.ontrack.feature.friend.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.friend.domain.FriendStatus
import de.ashman.ontrack.feature.friend.service.FriendRequestService
import de.ashman.ontrack.feature.friend.service.FriendService
import de.ashman.ontrack.feature.user.controller.dto.OtherUserDto
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import de.ashman.ontrack.feature.user.repository.UserService
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FriendController(
    private val userService: UserService,
    private val friendService: FriendService,
    private val friendRequestService: FriendRequestService,
) {
    @GetMapping("friends")
    fun findFriends(@AuthenticationPrincipal identity: Identity): ResponseEntity<List<UserDto>> {
        val user = userService.getById(identity.id)
        val friends = friendService.getFriends(user)
        val friendDtos = friends.map { it.toDto() }

        return ResponseEntity.ok(friendDtos)
    }

    @GetMapping("friends-and-requests")
    fun findFriendsAndFriendRequests(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<OtherUserDto>> {
        val user = userService.getById(identity.id)

        val friends = friendService.getFriends(user)
        val sentRequests = friendRequestService.findReceiversOfSentRequests(user)
        val receivedRequests = friendRequestService.findSendersOfReceivedRequests(user)

        val friendDtos = friends.map {
            OtherUserDto(it.toDto(), FriendStatus.FRIEND)
        }
        val sentRequestDtos = sentRequests.map {
            OtherUserDto(it.toDto(), FriendStatus.REQUEST_SENT)
        }
        val receivedRequestDtos = receivedRequests.map {
            OtherUserDto(it.toDto(), FriendStatus.REQUEST_RECEIVED)
        }

        return ResponseEntity.ok(friendDtos + sentRequestDtos + receivedRequestDtos)
    }

    @Transactional
    @DeleteMapping("friend/{id}")
    fun removeFriend(
        @PathVariable("id") friendId: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Unit> {
        val user = userService.getById(identity.id)
        val friend = userService.getById(friendId)

        friendService.endFriendship(user, friend)

        return ResponseEntity.ok().build()
    }
}

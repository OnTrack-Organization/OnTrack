package de.ashman.ontrack.feature.friend.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.friend.domain.FriendStatus
import de.ashman.ontrack.feature.friend.repository.FriendRequestService
import de.ashman.ontrack.feature.friend.repository.FriendshipService
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
    private val friendshipService: FriendshipService,
    private val friendRequestService: FriendRequestService,
) {
    @GetMapping("friends")
    fun findFriends(@AuthenticationPrincipal identity: Identity): ResponseEntity<List<UserDto>> {
        val friendIds = friendshipService.getFriendIds(identity.id)
        val friends = userService.findAllById(friendIds)
        val friendDtos = friends.stream().map { it.toDto() }.toList()

        return ResponseEntity.ok(friendDtos)
    }

    @GetMapping("friends-and-requests")
    fun findFriendsAndFriendRequests(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<OtherUserDto>> {
        val friendIds = friendshipService.getFriendIds(identity.id)
        val outcomingFriendIds = friendRequestService.findReceiversOfSentRequests(identity.id)
        val incomingFriendIds = friendRequestService.findSendersOfReceivedRequests(identity.id)

        val friends = userService.findAllById(friendIds)
        val outcomingFriends = userService.findAllById(outcomingFriendIds)
        val incomingFriends = userService.findAllById(incomingFriendIds)

        val friendDtos = friends.map {
            OtherUserDto(it.toDto(), FriendStatus.FRIEND)
        }
        val outcomingFriendDtos = outcomingFriends.map {
            OtherUserDto(it.toDto(), FriendStatus.REQUEST_SENT)
        }
        val incomingFriendDtos = incomingFriends.map {
            OtherUserDto(it.toDto(), FriendStatus.REQUEST_RECEIVED)
        }

        return ResponseEntity.ok(friendDtos + outcomingFriendDtos + incomingFriendDtos)
    }

    @Transactional
    @DeleteMapping("friend/{id}")
    fun removeFriend(
        @PathVariable("id") friendId: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Unit> {
        friendshipService.endFriendship(identity.id, friendId)

        return ResponseEntity.ok().build()
    }
}

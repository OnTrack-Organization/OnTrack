package de.ashman.ontrack.user.application.controller.friend

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.user.application.controller.FriendStatus
import de.ashman.ontrack.user.application.controller.OtherUserDto
import de.ashman.ontrack.user.application.controller.user.UserDto
import de.ashman.ontrack.user.application.controller.user.toDto
import de.ashman.ontrack.user.domain.repository.FriendRequestRepository
import de.ashman.ontrack.user.domain.repository.FriendshipRepository
import de.ashman.ontrack.user.domain.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FriendController(
    private val friendshipRepository: FriendshipRepository,
    private val userRepository: UserRepository,
    private val friendRequestRepository: FriendRequestRepository,
) {
    @GetMapping("friends")
    fun findFriends(@AuthenticationPrincipal identity: Identity): ResponseEntity<List<UserDto>>
    {
        val friendIds = friendshipRepository.getFriendIds(identity.id)
        val friends = userRepository.findAllById(friendIds)
        val friendDtos = friends.stream().map { it.toDto() }.toList()

        return ResponseEntity.ok(friendDtos)
    }

    @GetMapping("friends-and-requests")
    fun findFriendsAndFriendRequests(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<OtherUserDto>>
    {
        val friendIds = friendshipRepository.getFriendIds(identity.id)
        val outcomingFriendIds = friendRequestRepository.findReceiversOfSentRequests(identity.id)
        val incomingFriendIds = friendRequestRepository.findSendersOfReceivedRequests(identity.id)

        val friends = userRepository.findAllById(friendIds)
        val outcomingFriends = userRepository.findAllById(outcomingFriendIds)
        val incomingFriends = userRepository.findAllById(incomingFriendIds)

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
    ): ResponseEntity<Unit>
    {
        friendshipRepository.endFriendship(identity.id, friendId)

        return ResponseEntity.ok().build()
    }
}

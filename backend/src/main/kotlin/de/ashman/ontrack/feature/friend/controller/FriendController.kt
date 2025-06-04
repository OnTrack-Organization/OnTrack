package de.ashman.ontrack.feature.friend.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.friend.service.FriendService
import de.ashman.ontrack.feature.user.controller.dto.OtherUserDto
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/friend")
class FriendController(
    private val friendService: FriendService,
) {
    @GetMapping("/all")
    fun findFriends(@AuthenticationPrincipal identity: Identity): ResponseEntity<List<UserDto>> {
        val friends = friendService.getFriendDtos(identity.id)
        return ResponseEntity.ok(friends)
    }

    @GetMapping("/friends-and-requests")
    fun findFriendsAndFriendRequests(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<OtherUserDto>> {
        val result = friendService.getFriendsAndFriendRequests(identity.id)
        return ResponseEntity.ok(result)
    }

    @Transactional
    @DeleteMapping("/{id}")
    fun removeFriend(
        @PathVariable("id") friendId: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Unit> {
        friendService.removeFriend(identity.id, friendId)
        return ResponseEntity.ok().build()
    }
}

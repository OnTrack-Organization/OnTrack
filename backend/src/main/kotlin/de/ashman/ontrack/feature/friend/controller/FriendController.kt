package de.ashman.ontrack.feature.friend.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.friend.service.FriendService
import de.ashman.ontrack.feature.user.controller.dto.OtherUserDto
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/friend")
class FriendController(
    private val friendService: FriendService,
) {
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun findFriends(
        @AuthenticationPrincipal identity: Identity
    ): List<UserDto> {
        return friendService.getFriendDtos(identity.id)
    }

    @GetMapping("/friends-and-requests")
    @ResponseStatus(HttpStatus.OK)
    fun findFriendsAndFriendRequests(
        @AuthenticationPrincipal identity: Identity
    ): List<OtherUserDto> {
        return friendService.getFriendsAndFriendRequests(identity.id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeFriend(
        @PathVariable("id") friendId: String,
        @AuthenticationPrincipal identity: Identity
    ) {
        friendService.endFriendship(identity.id, friendId)
    }
}

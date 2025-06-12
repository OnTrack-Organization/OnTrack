package de.ashman.ontrack.feature.friend.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.friend.service.FriendRequestService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/friend-request")
class FriendRequestController(
    private val friendRequestService: FriendRequestService
) {
    @PostMapping("/send/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun sendFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ) {
        friendRequestService.sendRequest(identity.id, userId)
    }

    @PostMapping("/accept/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun acceptFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ) {
        friendRequestService.acceptRequest(identity.id, userId)
    }

    @PostMapping("/decline/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun declineFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ) {
        friendRequestService.declineRequest(identity.id, userId)
    }

    @PostMapping("/cancel/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelFriendRequest(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable userId: String
    ) {
        friendRequestService.cancelRequest(identity.id, userId)
    }
}

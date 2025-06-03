package de.ashman.ontrack.feature.user.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import de.ashman.ontrack.feature.tracking.repository.TrackingService
import de.ashman.ontrack.feature.friend.domain.FriendStatus
import de.ashman.ontrack.feature.friend.service.FriendRequestService
import de.ashman.ontrack.feature.friend.service.FriendService
import de.ashman.ontrack.feature.user.controller.dto.OtherUserDto
import de.ashman.ontrack.feature.user.controller.dto.UserProfileDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import de.ashman.ontrack.feature.user.repository.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val friendService: FriendService,
    private val friendRequestService: FriendRequestService,
    private val trackingService: TrackingService,
) {
    @GetMapping("/search")
    fun search(
        @RequestParam username: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<OtherUserDto>> {
        val currentUser = userService.getById(identity.id)

        val friends = friendService.getFriends(currentUser)
        val sentRequests = friendRequestService.findReceiversOfSentRequests(currentUser)
        val receivedRequests = friendRequestService.findSendersOfReceivedRequests(currentUser)

        val users = userService.searchByUsername(username = username, excludedId = currentUser.id)

        val searchResult = users.map { user ->
            val status = when (user) {
                in friends -> FriendStatus.FRIEND
                in sentRequests -> FriendStatus.REQUEST_SENT
                in receivedRequests -> FriendStatus.REQUEST_RECEIVED
                else -> FriendStatus.STRANGER
            }

            OtherUserDto(user = user.toDto(), friendStatus = status)
        }

        return ResponseEntity.ok(searchResult)
    }

    @GetMapping("/{id}")
    fun getProfile(
        @PathVariable id: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<UserProfileDto> {
        val currentUser = userService.getById(identity.id)
        val otherUser = userService.getById(id)

        val trackings = trackingService.getTrackingsByUserId(otherUser.id)

        val areFriends = friendService.areFriends(currentUser.id, otherUser.id)
        val sentRequest = friendRequestService.findBySenderAndReceiver(currentUser, otherUser)
        val receivedRequest = friendRequestService.findBySenderAndReceiver(otherUser, currentUser)

        val friendStatus = when {
            areFriends -> FriendStatus.FRIEND
            sentRequest != null -> FriendStatus.REQUEST_SENT
            receivedRequest != null -> FriendStatus.REQUEST_RECEIVED
            else -> FriendStatus.STRANGER
        }

        val profileDto = UserProfileDto(
            user = OtherUserDto(otherUser.toDto(), friendStatus),
            trackings = trackings.map { it.toDto() }
        )

        return ResponseEntity.ok(profileDto)
    }
}

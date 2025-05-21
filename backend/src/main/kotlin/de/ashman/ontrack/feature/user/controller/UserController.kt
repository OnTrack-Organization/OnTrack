package de.ashman.ontrack.feature.user.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import de.ashman.ontrack.feature.tracking.repository.TrackingService
import de.ashman.ontrack.feature.friend.domain.FriendStatus
import de.ashman.ontrack.feature.friend.repository.FriendRequestService
import de.ashman.ontrack.feature.friend.repository.FriendshipService
import de.ashman.ontrack.feature.user.controller.dto.OtherUserDto
import de.ashman.ontrack.feature.user.controller.dto.UserProfileDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import de.ashman.ontrack.feature.user.repository.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
    private val friendshipService: FriendshipService,
    private val friendRequestService: FriendRequestService,
    private val trackingService: TrackingService,
) {
    @GetMapping("user/search")
    fun search(
        @RequestParam username: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<OtherUserDto>> {
        val friendIds = friendshipService.getFriendIds(identity.id)
        val outcomingFriendIds = friendRequestService.findReceiversOfSentRequests(identity.id)
        val incomingFriendIds = friendRequestService.findSendersOfReceivedRequests(identity.id)

        // TODO right now this only returns users where the username matches exactly
        val users = userService.searchByUsername(username)

        // 3) map each User → UserSearchResult by checking membership in those sets
        val searchResult = users.map { user ->
            val status = when (user.id) {
                in friendIds -> FriendStatus.FRIEND
                in outcomingFriendIds -> FriendStatus.REQUEST_SENT
                in incomingFriendIds -> FriendStatus.REQUEST_RECEIVED
                else -> FriendStatus.STRANGER
            }
            OtherUserDto(user = user.toDto(), friendStatus = status)
        }

        return ResponseEntity.ok(searchResult)
    }

    @GetMapping("user/{id}")
    fun getProfile(
        @PathVariable("id") id: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<UserProfileDto> {
        val user = userService.getById(id)
        val trackings = trackingService.getTrackingsByUserId(user.id)

        val areFriends = friendshipService.areFriends(
            identity.id,
            user.id
        )
        val sentRequest = friendRequestService.findBySenderAndReceiver(identity.id, user.id)
        val receivedRequest = friendRequestService.findBySenderAndReceiver(user.id, identity.id)

        val friendStatus = when {
            areFriends -> FriendStatus.FRIEND
            sentRequest !== null -> FriendStatus.REQUEST_SENT
            receivedRequest !== null -> FriendStatus.REQUEST_RECEIVED
            else -> FriendStatus.STRANGER
        }

        val trackingDtos = trackings.map { it.toDto() }

        val profileDto = UserProfileDto(
            user = OtherUserDto(
                user.toDto(),
                friendStatus
            ),
            trackings = trackingDtos
        )

        return ResponseEntity.ok(profileDto)
    }
}

package de.ashman.ontrack.user.application.controller.user

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.tracking.application.controller.toDto
import de.ashman.ontrack.tracking.infrastructure.TrackingJpaRepository
import de.ashman.ontrack.user.application.controller.FriendShipStatus
import de.ashman.ontrack.user.application.controller.OtherUserDto
import de.ashman.ontrack.user.domain.repository.FriendRequestRepository
import de.ashman.ontrack.user.domain.repository.FriendshipRepository
import de.ashman.ontrack.user.domain.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository,
    private val friendRequestRepository: FriendRequestRepository,
    private val trackingRepository: TrackingJpaRepository
) {
    @GetMapping("user/search")
    fun search(
        @RequestParam username: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<OtherUserDto>> {
        val friendIds = friendshipRepository.getFriends(identity.id)
        val outcomingFriendIds = friendRequestRepository.findReceiversOfSentRequests(identity.id)
        val incomingFriendIds = friendRequestRepository.findSendersOfReceivedRequests(identity.id)

        val users = userRepository.searchByUsername(username)

        // 3) map each User → UserSearchResult by checking membership in those sets
        val searchResult = users.map { user ->
            val status = when (user.id) {
                in friendIds -> FriendShipStatus.FRIEND
                in outcomingFriendIds -> FriendShipStatus.REQUEST_SENT
                in incomingFriendIds -> FriendShipStatus.REQUEST_RECEIVED
                else -> FriendShipStatus.STRANGER
            }
            OtherUserDto(user.toUserDto(), status)
        }

        return ResponseEntity.ok(searchResult)
    }

    @GetMapping("user/{id}")
    fun getProfile(
        @PathVariable("id") id: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<UserProfileDto> {
        val user = userRepository.getById(id)
        val trackings = trackingRepository.getTrackingsByUserId(user.id)

        val areFriends = friendshipRepository.areFriends(
            identity.id,
            user.id
        )
        val sentRequest = friendRequestRepository.findBySenderAndReceiver(identity.id, user.id)
        val receivedRequest = friendRequestRepository.findBySenderAndReceiver(user.id, identity.id)
        val friendStatus = when {
            areFriends -> FriendShipStatus.FRIEND
            sentRequest !== null -> FriendShipStatus.REQUEST_SENT
            receivedRequest !== null -> FriendShipStatus.REQUEST_RECEIVED
            else -> FriendShipStatus.STRANGER
        }
        val trackingDtos = trackings.map { it.toDto() }
        val profileDto = UserProfileDto(
            OtherUserDto(
                user.toUserDto(),
                friendStatus
            ),
            trackingDtos
        )

        return ResponseEntity.ok(profileDto)
    }
}

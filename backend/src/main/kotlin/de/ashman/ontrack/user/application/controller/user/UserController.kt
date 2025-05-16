package de.ashman.ontrack.user.application.controller.user

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.tracking.application.controller.toDto
import de.ashman.ontrack.tracking.infrastructure.TrackingJpaRepository
import de.ashman.ontrack.user.application.controller.FriendStatus
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

        // TODO right now this only returns users where the username matches exactly
        val users = userRepository.searchByUsername(username)

        // 3) map each User → UserSearchResult by checking membership in those sets
        val searchResult = users.map { user ->
            val status = when (user.id) {
                in friendIds -> FriendStatus.FRIEND
                in outcomingFriendIds -> FriendStatus.REQUEST_SENT
                in incomingFriendIds -> FriendStatus.REQUEST_RECEIVED
                else -> FriendStatus.STRANGER
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
            areFriends -> FriendStatus.FRIEND
            sentRequest !== null -> FriendStatus.REQUEST_SENT
            receivedRequest !== null -> FriendStatus.REQUEST_RECEIVED
            else -> FriendStatus.STRANGER
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

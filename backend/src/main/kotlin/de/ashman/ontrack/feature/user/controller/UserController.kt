package de.ashman.ontrack.feature.user.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.user.controller.dto.OtherUserDto
import de.ashman.ontrack.feature.user.controller.dto.UserProfileDto
import de.ashman.ontrack.feature.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/search")
    fun search(
        @RequestParam username: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<OtherUserDto>> {
        val result = userService.searchOtherUsers(currentUserId = identity.id, searchUsername = username)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{id}")
    fun getProfile(
        @PathVariable id: String,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<UserProfileDto> {
        val profile = userService.getUserProfile(identity.id, id)
        return ResponseEntity.ok(profile)
    }
}

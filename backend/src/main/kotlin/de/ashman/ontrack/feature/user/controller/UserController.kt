package de.ashman.ontrack.feature.user.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.user.controller.dto.OtherUserDto
import de.ashman.ontrack.feature.user.controller.dto.UserProfileDto
import de.ashman.ontrack.feature.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    fun search(
        @RequestParam username: String,
        @AuthenticationPrincipal identity: Identity
    ): List<OtherUserDto> {
        return userService.searchOtherUsers(currentUserId = identity.id, searchUsername = username)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfile(
        @PathVariable id: String,
        @AuthenticationPrincipal identity: Identity
    ): UserProfileDto {
        return userService.getUserProfile(identity.id, id)
    }
}

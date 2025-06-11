package de.ashman.ontrack.feature.block.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.block.service.BlockService
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/block")
class BlockController(
    private val blockService: BlockService
) {
    @PostMapping("/{blockedId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun blockUser(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable blockedId: String
    ) {
        blockService.blockUser(identity.id, blockedId)
    }

    @DeleteMapping("/{blockedId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unblockUser(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable blockedId: String
    ) {
        blockService.unblockUser(identity.id, blockedId)
    }

    @GetMapping("/all")
    fun getBlockedUsers(
        @AuthenticationPrincipal identity: Identity
    ): List<UserDto> {
        val users = blockService.getBlockedUsers(identity.id)
        return users.map { it.toDto() }
    }
}

package de.ashman.ontrack.feature.block.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.block.service.BlockService
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/block")
class BlockController(
    private val blockService: BlockService
) {

    @PostMapping("/{blockedId}")
    fun blockUser(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable blockedId: String
    ): ResponseEntity<Unit> {
        blockService.blockUser(identity.id, blockedId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{blockedId}")
    fun unblockUser(
        @AuthenticationPrincipal identity: Identity,
        @PathVariable blockedId: String
    ): ResponseEntity<Unit> {
        blockService.unblockUser(identity.id, blockedId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/all")
    fun getBlockedUsers(
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<List<UserDto>> {
        val users = blockService.getBlockedUsers(identity.id)
        return ResponseEntity.ok(users.map { it.toDto() })
    }
}

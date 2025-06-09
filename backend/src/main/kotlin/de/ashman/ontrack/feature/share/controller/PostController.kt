package de.ashman.ontrack.feature.share.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.share.controller.dto.CommentsDto
import de.ashman.ontrack.feature.share.controller.dto.CreateCommentDto
import de.ashman.ontrack.feature.share.controller.dto.LikesDto
import de.ashman.ontrack.feature.share.controller.dto.PostDto
import de.ashman.ontrack.feature.share.service.PostService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/post")
class PostController(
    private val postService: PostService,
) {
    @GetMapping("/all")
    fun getPosts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Page<PostDto>> {
        val pageable = PageRequest.of(page, size)
        val posts = postService.getPosts(pageable, identity.id)

        return ResponseEntity.ok(posts)
    }

    @GetMapping("/{postId}")
    fun getPost(
        @PathVariable postId: UUID,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<PostDto> {
        val post = postService.getPost(postId, identity.id)

        return ResponseEntity.ok(post)
    }

    @PostMapping("/{postId}/like")
    fun toggleLike(
        @PathVariable postId: UUID,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<PostDto> {
        postService.toggleLike(postId, identity.id)
        val updatedPost = postService.getPost(postId, identity.id)

        return ResponseEntity.ok(updatedPost)
    }

    @PostMapping("/{postId}/comment")
    fun addComment(
        @PathVariable postId: UUID,
        @RequestBody @Valid dto: CreateCommentDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<PostDto> {
        postService.addComment(postId, identity.id, dto)
        val updatedPost = postService.getPost(postId, identity.id)

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedPost)
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    fun deleteComment(
        @PathVariable postId: UUID,
        @PathVariable commentId: UUID,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<PostDto> {
        postService.removeComment(postId, commentId, identity.id)
        val updatedPost = postService.getPost(postId, identity.id)

        return ResponseEntity.ok(updatedPost)
    }

    @GetMapping("/{postId}/comments")
    fun getComments(
        @PathVariable postId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @AuthenticationPrincipal identity: Identity,
    ): ResponseEntity<CommentsDto> {
        val pageable = PageRequest.of(page, size)
        val comments = postService.getComments(postId, identity.id, pageable)

        return ResponseEntity.ok(comments)
    }

    @GetMapping("/{postId}/likes")
    fun getLikes(
        @PathVariable postId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @AuthenticationPrincipal identity: Identity,
    ): ResponseEntity<LikesDto> {
        val pageable = PageRequest.of(page, size)
        val likes = postService.getLikes(postId, identity.id, pageable)

        return ResponseEntity.ok(likes)
    }
}

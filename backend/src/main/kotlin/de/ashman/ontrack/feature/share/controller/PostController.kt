package de.ashman.ontrack.feature.share.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.share.controller.dto.CommentDto
import de.ashman.ontrack.feature.share.controller.dto.CreateCommentDto
import de.ashman.ontrack.feature.share.controller.dto.LikeDto
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
@RequestMapping("posts")
class PostController(
    private val postService: PostService,
) {
    @GetMapping
    fun getFeed(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Page<PostDto>> {
        val pageable = PageRequest.of(page, size)
        val posts = postService.getPosts(pageable = pageable, currentUserId = identity.id)

        return ResponseEntity.ok(posts)
    }

    @GetMapping("/{postId}")
    fun getPost(
        @PathVariable postId: UUID,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<PostDto> {
        val post = postService.getPost(postId = postId, currentUserId = identity.id)

        return ResponseEntity.ok(post)
    }

    @PostMapping("/{postId}/like")
    fun toggleLike(
        @PathVariable postId: UUID,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<LikeDto> {
        val like = postService.toggleLike(postId = postId, userId = identity.id)

        return ResponseEntity.ok(like)
    }

    @PostMapping("/{postId}/comment")
    fun addComment(
        @PathVariable postId: UUID,
        @RequestBody @Valid dto: CreateCommentDto,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<CommentDto> {
        val comment = postService.addComment(postId = postId, userId = identity.id, dto = dto)

        return ResponseEntity.status(HttpStatus.CREATED).body(comment)
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    fun deleteComment(
        @PathVariable postId: UUID,
        @PathVariable commentId: UUID,
        @AuthenticationPrincipal identity: Identity
    ): ResponseEntity<Unit> {
        postService.removeComment(postId = postId, commentId = commentId, userId = identity.id)

        return ResponseEntity.ok().build()
    }

    @GetMapping("/{postId}/comments")
    fun getComments(
        @PathVariable postId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @AuthenticationPrincipal identity: Identity,
    ): ResponseEntity<Page<CommentDto>> {
        val pageable = PageRequest.of(page, size)
        val comments = postService.getComments(postId = postId, currentUserId = identity.id, pageable = pageable)

        return ResponseEntity.ok(comments)
    }

    @GetMapping("/{postId}/likes")
    fun getLikes(
        @PathVariable postId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @AuthenticationPrincipal identity: Identity,
    ): ResponseEntity<Page<LikeDto>> {
        val pageable = PageRequest.of(page, size)
        val likes = postService.getLikes(postId = postId, pageable = pageable)

        return ResponseEntity.ok(likes)
    }
}

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
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/post")
class PostController(
    private val postService: PostService,
) {
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getPosts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @AuthenticationPrincipal identity: Identity
    ): Page<PostDto> {
        val pageable = PageRequest.of(page, size)
        val posts = postService.getPosts(pageable, identity.id)

        return posts
    }

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    fun getPost(
        @PathVariable postId: UUID,
        @AuthenticationPrincipal identity: Identity
    ): PostDto {
        return postService.getPost(postId, identity.id)
    }

    @PostMapping("/{postId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    fun toggleLike(
        @PathVariable postId: UUID,
        @AuthenticationPrincipal identity: Identity
    ): PostDto {
        return postService.toggleLike(postId, identity.id)
    }

    @PostMapping("/{postId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    fun addComment(
        @PathVariable postId: UUID,
        @RequestBody @Valid dto: CreateCommentDto,
        @AuthenticationPrincipal identity: Identity
    ): PostDto {
        return postService.addComment(postId, identity.id, dto)
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(
        @PathVariable postId: UUID,
        @PathVariable commentId: UUID,
        @AuthenticationPrincipal identity: Identity
    ): PostDto {
        return postService.removeComment(postId, commentId, identity.id)
    }

    @GetMapping("/{postId}/comments")
    @ResponseStatus(HttpStatus.OK)
    fun getComments(
        @PathVariable postId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @AuthenticationPrincipal identity: Identity,
    ): CommentsDto {
        val pageable = PageRequest.of(page, size)
        val comments = postService.getComments(postId, identity.id, pageable)

        return comments
    }

    @GetMapping("/{postId}/likes")
    @ResponseStatus(HttpStatus.OK)
    fun getLikes(
        @PathVariable postId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @AuthenticationPrincipal identity: Identity,
    ): LikesDto {
        val pageable = PageRequest.of(page, size)
        val likes = postService.getLikes(postId, identity.id, pageable)

        return likes
    }
}

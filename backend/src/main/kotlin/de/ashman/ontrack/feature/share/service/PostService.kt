package de.ashman.ontrack.feature.share.service

import de.ashman.ontrack.feature.block.service.BlockingService
import de.ashman.ontrack.feature.friend.repository.FriendRepository
import de.ashman.ontrack.feature.notification.service.NotificationService
import de.ashman.ontrack.feature.review.domain.Review
import de.ashman.ontrack.feature.share.controller.dto.*
import de.ashman.ontrack.feature.share.domain.Comment
import de.ashman.ontrack.feature.share.domain.Like
import de.ashman.ontrack.feature.share.domain.Post
import de.ashman.ontrack.feature.share.repository.CommentRepository
import de.ashman.ontrack.feature.share.repository.LikeRepository
import de.ashman.ontrack.feature.share.repository.PostRepository
import de.ashman.ontrack.feature.tracking.domain.Tracking
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.util.*

@Service
class PostService(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository,
    private val blockingService: BlockingService,
    private val notificationService: NotificationService,
) {
    fun getPosts(pageable: Pageable, currentUserId: String): Page<PostDto> {
        val pageRequest = PageRequest.of(0, 3)
        val userIds = friendRepository.findFriendIdsOf(currentUserId) + currentUserId
        val posts = postRepository.findByUserIds(userIds, pageable)

        return posts.map { post ->
            val likeCount = likeRepository.countByPostId(post.id)
            val commentCount = commentRepository.countByPostId(post.id)
            val likedByCurrentUser = likeRepository.existsByPostIdAndUserId(post.id, currentUserId)

            val likes = likeRepository
                .findVisibleByPostId(post.id, currentUserId, pageRequest)
                .map { it.toDto() }
                .toList()

            val comments = commentRepository
                .findVisibleByPostId(post.id, currentUserId, pageRequest)
                .map { it.toDto(currentUserId = currentUserId, postOwnerId = post.user.id) }
                .toList()

            post.toDto(
                likes = likes,
                comments = comments,
                likedByCurrentUser = likedByCurrentUser,
                likeCount = likeCount,
                commentCount = commentCount,
            )
        }
    }

    fun getPost(postId: UUID, currentUserId: String): PostDto {
        val pageRequest = Pageable.unpaged()
        val post = postRepository.getReferenceById(postId)

        if (blockingService.isBlocked(post.user.id, currentUserId)) {
            throw AccessDeniedException("Cannot view post, because user is blocked")
        }

        val likeCount = likeRepository.countByPostId(post.id)
        val commentCount = commentRepository.countByPostId(post.id)
        val likedByCurrentUser = likeRepository.existsByPostIdAndUserId(postId, currentUserId)

        val likes = likeRepository
            .findVisibleByPostId(postId, currentUserId, pageRequest)
            .map { it.toDto() }
            .toList()

        val comments = commentRepository
            .findVisibleByPostId(postId, currentUserId, pageRequest)
            .map { it.toDto(currentUserId = currentUserId, postOwnerId = post.user.id) }
            .toList()

        return post.toDto(
            likes = likes,
            comments = comments,
            likedByCurrentUser = likedByCurrentUser,
            likeCount = likeCount,
            commentCount = commentCount,
        )
    }

    fun createPostForTracking(tracking: Tracking) {
        val post = Post(
            user = tracking.user,
            tracking = tracking,
        )

        postRepository.save(post)
    }

    fun updatePostWithReview(review: Review) {
        val post = postRepository.findByTrackingId(review.tracking.id) ?: throw IllegalStateException("No post found for tracking ${review.tracking.id}")

        val updatedPost = post.copy(
            review = review,
        )

        postRepository.save(updatedPost)
    }

    fun toggleLike(postId: UUID, likerUserId: String): LikeDto {
        val post = postRepository.getReferenceById(postId)

        if (blockingService.isBlocked(post.user.id, likerUserId)) {
            throw AccessDeniedException("Cannot toggle like, because user is blocked")
        }

        val existingLike = likeRepository.findByPostIdAndUserId(postId, likerUserId)

        val receiver = userRepository.getReferenceById(post.user.id)
        val sender = userRepository.getReferenceById(likerUserId)

        return if (existingLike != null) {
            likeRepository.delete(existingLike)

            existingLike.toDto()
        } else {
            val user = userRepository.getReferenceById(likerUserId)
            val like = Like(
                post = post,
                user = user
            )

            likeRepository.save(like)

            notificationService.createPostLiked(liker = sender, postOwner = receiver, post = post)

            like.toDto()
        }
    }

    fun addComment(postId: UUID, commenterUserId: String, dto: CreateCommentDto): CommentDto {
        val post = postRepository.getReferenceById(postId)

        if (blockingService.isBlocked(post.user.id, commenterUserId)) {
            throw AccessDeniedException("Cannot comment on post, because user is blocked")
        }

        val receiver = userRepository.getReferenceById(post.user.id)
        val sender = userRepository.getReferenceById(commenterUserId)

        val mentionedUsernames = parseMentionedUsernames(dto.message)
        val mentionedUsers = userRepository.findByUsernameIn(mentionedUsernames)

        // Filter out blocked users for mentions
        val visibleMentionedUsers = mentionedUsers.filterNot { mentionedUser ->
            blockingService.isBlocked(mentionedUser.id, commenterUserId) ||
                    blockingService.isBlocked(commenterUserId, mentionedUser.id)
        }.toSet()

        val comment = Comment(
            post = post,
            user = sender,
            message = dto.message,
            mentionedUsers = visibleMentionedUsers
        )

        commentRepository.save(comment)

        // Mentioned users get individual notifications
        if (visibleMentionedUsers.isNotEmpty()) {
            visibleMentionedUsers.forEach { mentionedUser ->
                notificationService.createPostMentioned(
                    commenter = sender,
                    postOwner = mentionedUser,
                    post = post,
                    comment = comment
                )
            }
        }

        // Post owner gets notified about a comment only if not already mentioned
        if (receiver !in visibleMentionedUsers) {
            notificationService.createPostCommented(
                commenter = sender,
                postOwner = receiver,
                post = post,
                comment = comment
            )
        }

        return comment.toDto(sender.id, post.user.id)
    }

    fun removeComment(postId: UUID, commentId: UUID, userId: String) {
        val comment = commentRepository.getReferenceById(commentId)

        if (comment.user.id != userId && comment.post.user.id != userId) {
            throw AccessDeniedException("Not authorized to delete this comment")
        }

        commentRepository.delete(comment)
    }

    fun getComments(postId: UUID, currentUserId: String, pageable: Pageable): CommentsDto {
        val post = postRepository.getReferenceById(postId)
        val commentCount = commentRepository.countByPostId(post.id)

        val comments = commentRepository.findVisibleByPostId(post.id, currentUserId, pageable)
        val commentDtos = comments.map { it.toDto(currentUserId, post.user.id) }

        val commentsDto = CommentsDto(
            comments = commentDtos,
            commentCount = commentCount,
        )

        return commentsDto
    }

    fun getLikes(postId: UUID, currentUserId: String, pageable: Pageable): LikesDto {
        val likeCount = likeRepository.countByPostId(postId)
        val likes = likeRepository.findVisibleByPostId(postId, currentUserId, pageable)
        val likeDtos = likes.map { it.toDto() }

        val likesDto = LikesDto(
            likes = likeDtos,
            likeCount = likeCount,
        )

        return likesDto
    }

    private fun parseMentionedUsernames(message: String): Set<String> {
        val regex = Regex("@([A-Za-z0-9_]+)")
        return regex.findAll(message)
            .map { it.groupValues[1] }
            .toSet()
    }
}

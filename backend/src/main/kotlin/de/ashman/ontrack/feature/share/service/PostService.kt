package de.ashman.ontrack.feature.share.service

import de.ashman.ontrack.feature.friend.repository.FriendRepository
import de.ashman.ontrack.feature.notification.service.NotificationService
import de.ashman.ontrack.feature.review.controller.dto.toDto
import de.ashman.ontrack.feature.review.domain.Review
import de.ashman.ontrack.feature.share.controller.dto.*
import de.ashman.ontrack.feature.share.domain.Comment
import de.ashman.ontrack.feature.share.domain.Like
import de.ashman.ontrack.feature.share.domain.Post
import de.ashman.ontrack.feature.share.repository.CommentRepository
import de.ashman.ontrack.feature.share.repository.LikeRepository
import de.ashman.ontrack.feature.share.repository.PostRepository
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import de.ashman.ontrack.feature.tracking.domain.Tracking
import de.ashman.ontrack.feature.user.controller.dto.toDto
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

            val likes = likeRepository.findByPostId(post.id, pageRequest)
                .map { it.toDto() }
                .toList()

            val comments = commentRepository.findByPostId(post.id, pageRequest)
                .map { it.toDto(currentUserId = currentUserId, postOwnerId = post.user.id) }
                .toList()

            PostDto(
                id = post.id,
                user = post.user.toDto(),
                tracking = post.tracking.toDto(),
                review = post.review?.toDto(),
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
        val likeCount = likeRepository.countByPostId(post.id)
        val commentCount = commentRepository.countByPostId(post.id)

        val likes = likeRepository.findByPostId(postId, pageRequest)
            .map { it.toDto() }
            .toList()

        val comments = commentRepository.findByPostId(postId, pageRequest)
            .map { it.toDto(currentUserId = currentUserId, postOwnerId = post.user.id) }
            .toList()

        val likedByCurrentUser = likeRepository.existsByPostIdAndUserId(postId, currentUserId)

        return PostDto(
            id = post.id,
            user = post.user.toDto(),
            tracking = post.tracking.toDto(),
            review = post.review?.toDto(),
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

        val updatedPost = post.copy(review = review)

        postRepository.save(updatedPost)
    }

    fun toggleLike(postId: UUID, likerUserId: String): LikeDto {
        val post = postRepository.getReferenceById(postId)
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

        val receiver = userRepository.getReferenceById(post.user.id)
        val sender = userRepository.getReferenceById(commenterUserId)

        val mentionedUsers = userRepository.findAllById(dto.mentionedUsers)

        val comment = Comment(
            post = post,
            user = sender,
            message = dto.message,
            mentionedUsers = mentionedUsers,
        )

        commentRepository.save(comment)

        notificationService.createPostCommented(commenter = sender, postOwner = receiver, post = post, comment = comment)

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
        val comments = commentRepository.findByPostId(post.id, pageable)

        val commentsDto = CommentsDto(
            comments = comments.map { it.toDto(currentUserId, post.user.id) },
            commentCount = commentCount,
        )

        return commentsDto
    }

    fun getLikes(postId: UUID, pageable: Pageable): LikesDto {
        val likeCount = likeRepository.countByPostId(postId)
        val likes = likeRepository.findByPostId(postId, pageable)

        val likesDto = LikesDto(
            likes = likes.map { it.toDto() },
            likeCount = likeCount,
        )

        return likesDto
    }

    fun deletePost(postId: UUID) {
        commentRepository.deleteAllByPostId(postId)
        likeRepository.deleteAllByPostId(postId)
        postRepository.deleteById(postId)
    }

    fun getPostIdByTrackingId(trackingId: UUID): UUID? = postRepository.findByTrackingId(trackingId)?.id
}

package de.ashman.ontrack.domain.share

import de.ashman.ontrack.domain.user.User

data class Comment(
    val id: String,
    val postId: String,
    val user: User,
    val mentionedUsers: List<User>,
    val message: String,
    val likeCount: Int,
    val postedByCurrentUser: Boolean,
    val deletable: Boolean,
    val timestamp: Long,
)
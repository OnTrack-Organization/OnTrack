package de.ashman.ontrack.domain.share

import de.ashman.ontrack.domain.user.User

data class Like(
    val id: String,
    val postId: String,
    val user: User,
    val timestamp: Long
)

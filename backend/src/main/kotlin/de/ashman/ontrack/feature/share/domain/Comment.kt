package de.ashman.ontrack.feature.share.domain

import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "comments")
data class Comment(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val post: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "comment_mentioned_users",
        joinColumns = [JoinColumn(name = "comment_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val mentionedUsers: List<User> = emptyList(),

    val message: String,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now()
)

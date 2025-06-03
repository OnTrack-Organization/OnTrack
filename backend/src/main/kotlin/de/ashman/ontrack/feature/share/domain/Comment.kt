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

    val message: String,

    @ManyToMany
    @JoinTable(
        name = "comment_mentions",
        joinColumns = [JoinColumn(name = "comment_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val mentionedUsers: Set<User> = setOf(),

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now()
)

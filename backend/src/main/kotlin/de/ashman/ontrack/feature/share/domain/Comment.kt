package de.ashman.ontrack.feature.share.domain

import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "comments")
data class Comment(
    @Id
    val id: UUID = UUID.randomUUID(),

    val message: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val post: Post,

    @ManyToMany
    @JoinTable(
        name = "comment_mentions",
        joinColumns = [JoinColumn(name = "comment_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val mentionedUsers: Set<User> = setOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),
)

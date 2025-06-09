package de.ashman.ontrack.feature.block.domain

import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "blockings",
    uniqueConstraints = [UniqueConstraint(columnNames = ["blocker_id", "blocked_id"])]
)
data class Blocking(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val blocker: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val blocked: User,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),
)

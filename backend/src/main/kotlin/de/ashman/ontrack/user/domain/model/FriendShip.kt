package de.ashman.ontrack.user.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

@Entity
@Table(
    name = "friendships",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id1", "user_id2"], name = "uniq_friends")]
)
class Friendship private constructor(
    @Column(name = "user_id1", nullable = false)
    val userId1: String,

    @Column(name = "user_id2", nullable = false)
    val userId2: String
) {
    @Id
    @Column(name = "id", nullable = false)
    private val id: UUID = UUID.randomUUID()

    companion object {
        fun begin(a: String, b: String): Friendship {
            require(a != b) { "Cannot be friends with yourself" }

            // Lexicographical ordering
            val (first, second) = if (a < b) a to b else b to a

            return Friendship(first, second)
        }
    }
}

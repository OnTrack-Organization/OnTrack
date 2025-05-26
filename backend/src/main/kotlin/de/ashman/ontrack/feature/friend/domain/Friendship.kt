package de.ashman.ontrack.feature.friend.domain

import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "friendships",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user1_id", "user2_id"], name = "uniq_friends")]
)
data class Friendship(
    @Id
    private val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val user1: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val user2: User
) {
    companion object {
        fun begin(a: User, b: User): Friendship {
            return if (a.id < b.id) {
                Friendship(user1 = a, user2 = b)
            } else {
                Friendship(user1 = b, user2 = a)
            }
        }
    }
}

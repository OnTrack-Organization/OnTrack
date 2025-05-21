package de.ashman.ontrack.feature.friend.repository

import de.ashman.ontrack.feature.friend.domain.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FriendshipRepository : JpaRepository<Friendship, UUID> {
    @Query(
        "select " +
                "case when f.userId1 = :user_id then f.userId2 else f.userId1 end " +
                "from Friendship f " +
                "where :user_id in (f.userId1, f.userId2)"
    )
    fun findFriendsOfUser(@Param("user_id") userId: String): List<String>

    fun deleteByUserId1AndUserId2(userId1: String, userId2: String)

    fun existsByUserId1AndUserId2(userId1: String, userId2: String): Boolean
}



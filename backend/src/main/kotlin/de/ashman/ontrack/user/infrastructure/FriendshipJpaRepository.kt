package de.ashman.ontrack.user.infrastructure

import de.ashman.ontrack.user.domain.model.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface FriendshipJpaRepository : JpaRepository<Friendship, UUID> {

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



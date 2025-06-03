package de.ashman.ontrack.feature.friend.repository

import de.ashman.ontrack.feature.friend.domain.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FriendRepository : JpaRepository<Friendship, UUID> {
    @Query(
        """
    SELECT 
        CASE 
            WHEN f.user1.id = :userId THEN f.user2.id 
            ELSE f.user1.id 
        END 
    FROM Friendship f 
    WHERE :userId IN (f.user1.id, f.user2.id)"""
    )
    fun findFriendIdsOf(userId: String): List<String>

    fun deleteByUser1IdAndUser2Id(userId1: String, userId2: String)

    fun existsByUser1IdAndUser2Id(userId1: String, userId2: String): Boolean

    fun deleteAllByUser1IdOrUser2Id(userId1: String, userId2: String)
}



package de.ashman.ontrack.feature.friend.repository

import de.ashman.ontrack.feature.friend.domain.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

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

    @Query(
        """
        SELECT 
            CASE WHEN COUNT(f) > 0 THEN true ELSE false END 
        FROM Friendship f 
        WHERE (f.user1.id = :userId1 AND f.user2.id = :userId2)
           OR (f.user1.id = :userId2 AND f.user2.id = :userId1)
    """
    )
    fun areFriends(userId1: String, userId2: String): Boolean

    fun deleteByUser1IdAndUser2Id(userId1: String, userId2: String)
}



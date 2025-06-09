package de.ashman.ontrack.feature.friend.repository

import de.ashman.ontrack.feature.friend.domain.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
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
        WHERE (f.user1.id = :userId AND f.user2.id = :friendId)
           OR (f.user1.id = :friendId AND f.user2.id = :userId)
    """
    )
    fun areFriends(userId: String, friendId: String): Boolean

    @Modifying
    @Query(
        """
    DELETE FROM Friendship f
    WHERE (f.user1.id = :userId AND f.user2.id = :friendId)
       OR (f.user1.id = :friendId AND f.user2.id = :userId)
        """
    )
    fun deleteFriendshipBetween(userId: String, friendId: String)

}



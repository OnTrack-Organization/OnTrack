package de.ashman.ontrack.feature.share.repository

import de.ashman.ontrack.feature.share.domain.Like
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface LikeRepository : JpaRepository<Like, UUID> {
    fun countByPostId(postId: UUID): Int
    fun existsByPostIdAndUserId(postId: UUID, userId: String): Boolean
    fun findByPostIdAndUserId(postId: UUID, userId: String): Like?

    @Query("""
    SELECT l FROM Like l 
    WHERE l.post.id = :postId 
      AND l.user.id NOT IN (
          SELECT b.blocked.id FROM Blocking b WHERE b.blocker.id = :currentUserId
          UNION
          SELECT b.blocker.id FROM Blocking b WHERE b.blocked.id = :currentUserId
      )
""")
    fun findVisibleByPostId(
        postId: UUID,
        currentUserId: String,
        pageable: Pageable
    ): Page<Like>

}
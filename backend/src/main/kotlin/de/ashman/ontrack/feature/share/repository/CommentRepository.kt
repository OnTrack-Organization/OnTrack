package de.ashman.ontrack.feature.share.repository

import de.ashman.ontrack.feature.share.domain.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface CommentRepository : JpaRepository<Comment, UUID> {
    fun countByPostId(postId: UUID): Int

    @Query(
        """
    SELECT c FROM Comment c 
    WHERE c.post.id = :postId 
      AND c.user.id NOT IN (
          SELECT b.blocked.id FROM Blocking b WHERE b.blocker.id = :currentUserId
          UNION
          SELECT b.blocker.id FROM Blocking b WHERE b.blocked.id = :currentUserId
      )"""
    )
    fun findVisibleByPostId(
        postId: UUID,
        currentUserId: String,
        pageable: Pageable
    ): Page<Comment>

}
package de.ashman.ontrack.feature.share.repository

import de.ashman.ontrack.feature.share.domain.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface CommentRepository : JpaRepository<Comment, UUID> {
    fun findByPostId(postId: UUID, pageable: Pageable): Page<Comment>
    fun countByPostId(postId: UUID): Int
    fun deleteAllByPostId(postId: UUID)
    fun deleteAllByUserId(userId: String)

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.user.id = :userId")
    fun deleteAllByPostOwnerId(userId: String)
}
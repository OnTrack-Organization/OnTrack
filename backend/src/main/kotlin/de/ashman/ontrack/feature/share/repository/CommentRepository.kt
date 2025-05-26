package de.ashman.ontrack.feature.share.repository

import de.ashman.ontrack.feature.share.domain.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CommentRepository : JpaRepository<Comment, UUID> {
    fun findByPostId(postId: UUID, pageable: Pageable): Page<Comment>
    fun countByPostId(postId: UUID): Int
}
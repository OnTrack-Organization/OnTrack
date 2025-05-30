package de.ashman.ontrack.feature.share.repository

import de.ashman.ontrack.feature.share.domain.Like
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LikeRepository : JpaRepository<Like, UUID> {
    fun findByPostId(postId: UUID, pageable: Pageable): Page<Like>
    fun countByPostId(postId: UUID): Int
    fun existsByPostIdAndUserId(postId: UUID, userId: String): Boolean
    fun findByPostIdAndUserId(postId: UUID, userId: String): Like?
    fun deleteAllByPostId(postId: UUID)
}
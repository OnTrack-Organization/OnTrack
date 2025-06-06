package de.ashman.ontrack.feature.share.repository

import de.ashman.ontrack.feature.share.domain.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface PostRepository : JpaRepository<Post, UUID> {
    @Query("SELECT post FROM Post post WHERE post.user.id IN :userIds ORDER BY post.updatedAt DESC")
    fun findByUserIds(userIds: List<String>, pageable: Pageable): Page<Post>
    fun findByTrackingId(trackingId: UUID): Post?
}

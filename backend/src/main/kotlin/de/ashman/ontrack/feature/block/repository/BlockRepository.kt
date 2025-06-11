package de.ashman.ontrack.feature.block.repository

import de.ashman.ontrack.feature.block.domain.Blocking
import de.ashman.ontrack.feature.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BlockRepository : JpaRepository<Blocking, UUID> {
    fun existsByBlockerAndBlocked(blocker: User, blocked: User): Boolean

    fun deleteByBlockerIdAndBlockedId(blockerId: String, blockedId: String)

    @Query("SELECT b.blocked FROM Blocking b WHERE b.blocker.id = :blockerId")
    fun findBlockedUsersByBlockerId(blockerId: String): Set<User>
}

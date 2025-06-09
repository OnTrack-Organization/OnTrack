package de.ashman.ontrack.feature.notification.repository

import Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface NotificationRepository : JpaRepository<Notification, String> {

    @Query(
        """
    SELECT n FROM Notification n
    WHERE n.receiver.id = :receiverId
      AND NOT EXISTS (
        SELECT 1 FROM Blocking b
        WHERE (b.blocker.id = :receiverId AND b.blocked.id = n.sender.id)
           OR (b.blocker.id = n.sender.id AND b.blocked.id = :receiverId)
      )
    ORDER BY n.updatedAt DESC
    """
    )
    fun findTop50VisibleByReceiverId(receiverId: String): List<Notification>

    fun findByIdAndReceiverId(id: UUID, receiverId: String): Notification

    @Modifying
    @Transactional
    @Query("update Notification n set n.read = true where n.receiver.id = :receiverId and n.read = false")
    fun markAllAsReadByReceiverId(receiverId: String): Int
}

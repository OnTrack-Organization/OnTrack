package de.ashman.ontrack.feature.notification.repository

import Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, String> {

    fun findTop50ByReceiverIdOrderByCreatedAtDesc(receiverId: String): List<Notification>

    @Modifying
    @Transactional
    @Query("update Notification n set n.read = true where n.id = :id and n.receiver.id = :receiverId")
    fun markAsRead(receiverId: String, id: UUID)
}

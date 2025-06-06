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

    fun findTop50ByReceiverIdOrderByUpdatedAtDesc(receiverId: String): List<Notification>

    fun findByIdAndReceiverId(id: UUID, receiverId: String): Notification

    @Modifying
    @Transactional
    @Query("update Notification n set n.read = true where n.receiver.id = :receiverId and n.read = false")
    fun markAllAsReadByReceiverId(receiverId: String): Int
}

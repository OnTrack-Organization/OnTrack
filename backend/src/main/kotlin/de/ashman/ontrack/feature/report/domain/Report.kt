package de.ashman.ontrack.feature.report.domain

import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "reports")
data class Report(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    val type: ReportType,

    @Enumerated(EnumType.STRING)
    val reason: ReportReason,

    val message: String? = null,

    @Column(name = "reported_content_id")
    val reportedContentId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    val reported: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val reporter: User,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: Instant = Instant.now()
)

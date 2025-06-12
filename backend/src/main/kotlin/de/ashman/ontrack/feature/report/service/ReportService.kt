package de.ashman.ontrack.feature.report.service

import de.ashman.ontrack.feature.report.domain.Report
import de.ashman.ontrack.feature.report.domain.ReportReason
import de.ashman.ontrack.feature.report.domain.ReportType
import de.ashman.ontrack.feature.report.repository.ReportRepository
import de.ashman.ontrack.feature.share.repository.PostRepository
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ReportService(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) {
    @Transactional
    fun report(
        reporterId: String,
        reportedId: String,
        reportedContentId: UUID?,
        type: ReportType,
        reason: ReportReason,
        description: String?,
    ) {
        val reporter = userRepository.findById(reporterId).orElseThrow { IllegalArgumentException("Reporter not found") }
        val reported = userRepository.findById(reportedId).orElseThrow { IllegalArgumentException("Reported user not found") }

        if (reporter.id == reported.id) {
            throw IllegalArgumentException("You cannot report yourself.")
        }

        // Optional content existence validation
        if (reportedContentId != null) {
            when (type) {
                ReportType.POST -> {
                    if (!postRepository.existsById(reportedContentId)) {
                        throw IllegalArgumentException("Reported post not found")
                    }
                }
                // TODO Add more cases like COMMENT in the future maybe
                else -> {}
            }
        }

        val report = Report(
            type = type,
            reason = reason,
            message = description,
            reportedContentId = reportedContentId,
            reported = reported,
            reporter = reporter,
        )

        reportRepository.save(report)
    }
}

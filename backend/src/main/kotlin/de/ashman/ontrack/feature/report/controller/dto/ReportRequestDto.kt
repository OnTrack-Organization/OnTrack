package de.ashman.ontrack.feature.report.controller.dto

import de.ashman.ontrack.feature.report.domain.ReportReason
import de.ashman.ontrack.feature.report.domain.ReportType
import java.util.*

data class ReportRequestDto(
    val reportedId: String,
    val type: ReportType,
    val reason: ReportReason,
    val reportedContentId: UUID?,
    val message: String?,
)

package de.ashman.ontrack.network.services.report.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReportRequestDto(
    val reportedId: String,
    val type: ReportType,
    val reason: ReportReason,
    val reportedContentId: String? = null,
    val message: String? = null,
)

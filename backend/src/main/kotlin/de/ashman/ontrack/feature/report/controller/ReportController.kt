package de.ashman.ontrack.feature.report.controller

import de.ashman.ontrack.config.Identity
import de.ashman.ontrack.feature.report.controller.dto.ReportRequestDto
import de.ashman.ontrack.feature.report.service.ReportService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/report")
class ReportController(
    private val reportService: ReportService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun report(
        @AuthenticationPrincipal identity: Identity,
        @RequestBody @Valid request: ReportRequestDto,
    ) {
        reportService.report(
            reporterId = identity.id,
            reportedId = request.reportedId,
            reportedContentId = request.reportedContentId,
            type = request.type,
            reason = request.reason,
            description = request.message
        )
    }
}

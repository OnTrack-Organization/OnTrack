package de.ashman.ontrack.feature.report.repository

import de.ashman.ontrack.feature.report.domain.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : JpaRepository<Report, String>

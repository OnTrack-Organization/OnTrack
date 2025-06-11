package de.ashman.ontrack.network.services.report

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.network.services.report.dto.ReportRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

interface ReportService {
    suspend fun report(dto: ReportRequestDto): Result<Unit>
}

class ReportServiceImpl(
    private val httpClient: HttpClient,
) : ReportService {
    override suspend fun report(dto: ReportRequestDto): Result<Unit> = safeApiCall {
        httpClient.post("/report") {
            setBody(dto)
        }
    }
}

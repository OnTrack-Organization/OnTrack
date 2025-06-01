package de.ashman.ontrack.api.utils

import co.touchlab.kermit.Logger
import de.ashman.ontrack.api.book.cleanupDescription
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.pow
import kotlin.math.roundToInt

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        val result = apiCall()
        //Logger.d { "API call successful: $result" }
        Result.success(result)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Logger.e(e) { "API call failed: $e" }
        Result.failure(e)
    }
}

data class ApiResponse<T>(
    val data: T,
    val status: HttpStatusCode
)

suspend inline fun <reified T> safeBackendApiCall(call: suspend () -> HttpResponse): Result<ApiResponse<T>> {
    return try {
        val response = call()

        if (response.status == HttpStatusCode.Unauthorized) {
            return Result.failure(Exception("Unauthorized"))
        }

        Result.success(
            ApiResponse(
                data = response.body(),
                status = response.status
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun getLivingDates(birthDate: String?, deathDate: String?): String? =
    listOfNotNull(birthDate, deathDate)
        .takeIf { it.isNotEmpty() }
        ?.joinToString(" - ")

fun String.formatDates(): String? {
    val inputFormats = listOf(
        "yyyy-MM-dd",
        "d MMMM yyyy",
    )

    inputFormats.forEach {
        try {
            val date: LocalDate = when (it) {
                "yyyy-MM-dd" -> LocalDate.parse(this)
                "d MMMM yyyy" -> {
                    val parts = this.split(" ")
                    val day = parts[0].toInt()
                    val month = Month.valueOf(parts[1].uppercase())
                    val year = parts[2].toInt()
                    LocalDate(year, month.number, day)
                }

                else -> throw IllegalArgumentException("Unsupported format: $it")
            }

            return "${date.dayOfMonth.toString().padStart(2, '0')}." +
                    "${date.monthNumber.toString().padStart(2, '0')}." +
                    "${date.year}".cleanupDescription()
        } catch (_: Exception) {
        }
    }
    return null
}

fun Double.roundDecimals(decimals: Int): String {
    val integerDigits = this.toInt()
    val doubleDigits = ((this - integerDigits) * 10f.pow(decimals)).roundToInt()

    return if (doubleDigits == 0 && decimals == 0) {
        integerDigits.toString()
    } else {
        "${integerDigits}.${doubleDigits}"
    }
}

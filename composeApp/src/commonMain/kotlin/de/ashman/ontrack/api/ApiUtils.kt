package de.ashman.ontrack.api

import co.touchlab.kermit.Logger
import de.ashman.ontrack.api.book.cleanupDescription
import de.ashman.ontrack.domain.Director
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        val result = apiCall()
        Logger.d { "API call successful: $result" }
        Result.success(result)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Logger.e(e) { "API call failed: $e" }
        Result.failure(e)
    }
}

fun String?.getTMDBCoverUrl(): String = this?.let { "https://image.tmdb.org/t/p/original${it}" }.orEmpty()

fun Int?.getOpenLibraryCoverUrl(): String = this?.let { "https://covers.openlibrary.org/b/id/${it}-L.jpg" }.orEmpty()

fun String?.getIGDBCoverUrl(): String = this?.let { "https://$it".replace("t_thumb", "t_1080p") }.orEmpty()

fun String.formatCreatorDate(): String? {
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
                    "${date.year}"
                        .cleanupDescription()
        } catch (_: Exception) {
        }
    }
    return null
}

fun Director.getLivingDates(): String? =
    listOfNotNull(birthDate, deathDate)
        .takeIf { it.isNotEmpty() }?.joinToString(" - ")
package de.ashman.ontrack.api.book

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.book.dto.BookSearchResponseDto
import de.ashman.ontrack.api.book.dto.BookTrendingResponseDto
import de.ashman.ontrack.api.book.dto.BookWorksResponseDto
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class BookRepository(
    private val httpClient: HttpClient,
) : MediaRepository {

    override suspend fun fetchByQuery(query: String): Result<List<Book>> {
        return safeApiCall {
            val response: BookSearchResponseDto = httpClient.get("search.json") {
                parameter("title", query)
                //parameter("fields", "key, title, cover_i")
                parameter("language", "eng")
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()

            response.books.map { it.toDomain() }
        }
    }

    override suspend fun fetchDetails(media: Media): Result<Book> {
        return safeApiCall {
            val response: BookWorksResponseDto = httpClient.get("/works/${media.id}.json").body()
            val bookDescription = response.description?.cleanupDescription()

            (media as Book).copy(
                description = bookDescription,
            )
        }
    }

    // TODO trending doesnt return all the data needed like search or details. need to call those in detailviewmodel to collect all the data
    override suspend fun fetchTrending(): Result<List<Book>> {
        return safeApiCall {
            val response: BookTrendingResponseDto = httpClient.get("trending/monthly.json") {
                // add fields as soon as openlibrary allows it
                //parameter("fields", "key, title, cover_i")
                parameter("language", "eng")
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()

            response.books.map { it.toDomain() }
        }
    }
}

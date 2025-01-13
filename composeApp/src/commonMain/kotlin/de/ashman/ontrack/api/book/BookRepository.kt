package de.ashman.ontrack.api.book

import de.ashman.ontrack.media.model.Book
import de.ashman.ontrack.api.book.dto.BookSearchResponseDto
import de.ashman.ontrack.api.book.dto.BookWorksResponseDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.book.dto.BookTrendingResponseDto
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.media.model.Media
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
                parameter("fields", "key, title, cover_i")
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()

            response.books.map { it.toDomain() }
        }
    }

    override suspend fun fetchDetails(id: String): Result<Book> {
        return safeApiCall {
            val response: BookWorksResponseDto = httpClient.get("$id.json") {}.body()

            response.toDomain()
        }
    }

    // TODO trending doesnt return all the data needed like search or details. need to call those in detailviewmodel to collect all the data
    override suspend fun fetchTrending(): Result<List<Book>> {
        return safeApiCall {
            val response: BookTrendingResponseDto = httpClient.get("trending/monthly.json") {
                // TODO add fields as soon as openlibrary allows it
                //parameter("fields", "key, title, cover_i")
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()

            response.books.map { it.toDomain() }
        }
    }

    suspend fun fetchMediaDetailsWithPartial(partialBook: Book): Result<Book> {
        return safeApiCall {
            val response: BookWorksResponseDto = httpClient.get("${partialBook.id}.json").body()
            val detailedBook = response.toDomain()

            partialBook.copy(
                description = detailedBook.description,
            )
        }
    }

}

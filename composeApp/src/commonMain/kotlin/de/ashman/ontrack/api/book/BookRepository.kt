package de.ashman.ontrack.api.book

import de.ashman.ontrack.media.model.Book
import de.ashman.ontrack.api.book.dto.BookSearchResponseDto
import de.ashman.ontrack.api.book.dto.BookWorksResponseDto
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.album.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class BookRepository(
    private val httpClient: HttpClient,
) : MediaRepository<Book> {

    override suspend fun fetchMediaByQuery(query: String): Result<List<Book>> {
        return safeApiCall {
            val response: BookSearchResponseDto = httpClient.get("search.json") {
                parameter("title", query)
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }.body()

            response.books.map { it.toDomain() }
        }
    }

    override suspend fun fetchMediaDetails(id: String): Result<Book> {
        return safeApiCall {
            val response: BookWorksResponseDto = httpClient.get("$id.json") {}.body()

            response.toDomain()
        }
    }

    suspend fun fetchMediaDetailsWithPartial(id: String, partialBook: Book?): Result<Book> {
        return safeApiCall {
            val response: BookWorksResponseDto = httpClient.get("$id.json").body()
            val detailedBook = response.toDomain()

            println("RESPONSE: $response")
            println("DETAILED BOOK: $detailedBook")
            println("PARTIAL BOOK: $partialBook")

            partialBook?.copy(
                description = detailedBook.description,
            ) ?: detailedBook
        }
    }

}

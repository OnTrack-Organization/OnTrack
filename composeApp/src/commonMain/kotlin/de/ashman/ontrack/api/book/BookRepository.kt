package de.ashman.ontrack.api.book

import de.ashman.ontrack.media.domain.Book
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
        // TODO CHANGE SO THAT WE ONLY GET TITLE AND COVER URL HERE!!! REST COMES FROM MEDIA DETAILS AFTERWARDS
        return safeApiCall {
            val response: BookSearchResponseDto = httpClient.get("search.json") {
                //parameter("fields", "title, cover_i, key")
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
}

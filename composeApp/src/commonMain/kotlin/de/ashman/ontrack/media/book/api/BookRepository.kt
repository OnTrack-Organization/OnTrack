package de.ashman.ontrack.media.book.api

import de.ashman.ontrack.media.model.Book
import de.ashman.ontrack.media.book.api.dto.BookSearchResponseDto
import de.ashman.ontrack.media.book.api.dto.BookWorksResponseDto
import de.ashman.ontrack.media.MediaRepository
import de.ashman.ontrack.media.album.api.safeApiCall
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
                parameter("limit", "3")
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

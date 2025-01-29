package de.ashman.ontrack.api.book

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.book.dto.AuthorDto
import de.ashman.ontrack.api.book.dto.AuthorWorksResponse
import de.ashman.ontrack.api.book.dto.BookSearchResponse
import de.ashman.ontrack.api.book.dto.BookTrendingResponse
import de.ashman.ontrack.api.book.dto.BookWorksResponse
import de.ashman.ontrack.api.utils.cleanupDescription
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.di.SMALL_FETCH_LIMIT
import de.ashman.ontrack.domain.Author
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class BookRepository(
    private val httpClient: HttpClient,
) : MediaRepository {

    override suspend fun fetchByQuery(query: String): Result<List<Book>> = safeApiCall {
        val response: BookSearchResponse = httpClient.get("search.json") {
            parameter("title", query)
            //parameter("fields", "key, title, cover_i")
            parameter("language", "eng")
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body()

        response.books.map { it.toDomain() }
    }

    override suspend fun fetchDetails(media: Media): Result<Book> = safeApiCall {
        val book = media as Book

        val description = fetchDescription(book.id)
        val author = fetchAuthorWithBooks(book.author.id)

        book.copy(
            description = description,
            author = author ?: book.author,
        )
    }

    // trending doesnt return all the data needed like search or details.
    // need to call those in detailviewmodel to collect all the data
    override suspend fun fetchTrending(): Result<List<Book>> = safeApiCall {
        val response: BookTrendingResponse = httpClient.get("trending/monthly.json") {
            // add fields as soon as openlibrary allows it
            //parameter("fields", "key, title, cover_i")
            parameter("language", "eng")
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body()

        response.books.map { it.toDomain() }
    }

    private suspend fun fetchDescription(bookId: String): String? {
        val response: BookWorksResponse = httpClient.get("/works/$bookId.json").body()
        return response.description?.cleanupDescription()
    }

    private suspend fun fetchAuthorWithBooks(authorId: String): Author? {
        val authorDto: AuthorDto = httpClient.get("authors/$authorId.json").body()
        val authorWorksResponse: AuthorWorksResponse = httpClient.get("authors/$authorId/works.json") {
            parameter("limit", SMALL_FETCH_LIMIT)
        }.body()
        val books = authorWorksResponse.entries.map { it.toDomain() }
        val booksCount = authorWorksResponse.size

        return authorDto.toDomain().copy(books = books, booksCount = booksCount)
    }
}

package de.ashman.ontrack.api.book

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.book.dto.AuthorDto
import de.ashman.ontrack.api.book.dto.AuthorWorksResponse
import de.ashman.ontrack.api.book.dto.BookEditionsEntry
import de.ashman.ontrack.api.book.dto.BookEditionsResponse
import de.ashman.ontrack.api.book.dto.BookRatingsResponse
import de.ashman.ontrack.api.book.dto.BookSearchResponse
import de.ashman.ontrack.api.book.dto.BookTrendingResponse
import de.ashman.ontrack.api.book.dto.BookWorksResponse
import de.ashman.ontrack.api.book.dto.RatingSummary
import de.ashman.ontrack.api.utils.extractPageCount
import de.ashman.ontrack.api.utils.extractPublishYear
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.di.SMALL_FETCH_LIMIT
import de.ashman.ontrack.domain.media.Author
import de.ashman.ontrack.domain.media.Book
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class BookRepository(
    private val httpClient: HttpClient,
) : MediaRepository {

    override suspend fun fetchTrending(): Result<List<Book>> = safeApiCall {
        httpClient.get("trending/weekly.json") {
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body<BookTrendingResponse>().books.map { it.toDomain() }
    }

    override suspend fun fetchByQuery(query: String): Result<List<Book>> = safeApiCall {
        httpClient.get("search.json") {
            parameter("title", query)
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.body<BookSearchResponse>().books.map { it.toDomain() }
    }

    override suspend fun fetchDetails(mediaId: String): Result<Book> = safeApiCall {
        coroutineScope {
            val book = fetchBook(mediaId)

            val authorDeferred = async { fetchAuthorWithBooks(book.author?.id) }
            val ratingsDeferred = async { fetchRatings(book.id) }
            val editionsDeferred = async { fetchEditions(book.id) }

            val author = authorDeferred.await()
            val ratings = ratingsDeferred.await()
            val editions = editionsDeferred.await()

            book.copy(
                author = author ?: book.author,
                apiRating = ratings.average,
                apiRatingCount = ratings.count,
                releaseYear = editions.extractPublishYear(),
                numberOfPages = editions.extractPageCount(),
            )
        }
    }

    private suspend fun fetchBook(bookId: String): Book {
        // 1. CALL
        val response: BookWorksResponse = httpClient.get("works/$bookId.json").body()
        return response.toDomain().copy()
    }

    private suspend fun fetchAuthorWithBooks(authorId: String?): Author? {
        authorId ?: return null

        // 2. CALL
        val authorDto: AuthorDto = httpClient.get("authors/$authorId.json").body()
        // 3. CALL
        val response: AuthorWorksResponse = httpClient.get("authors/$authorId/works.json") {
            parameter("limit", SMALL_FETCH_LIMIT)
        }.body()

        return authorDto.toDomain().copy(
            books = response.entries.map { it.toDomain() },
            booksCount = response.size
        )
    }

    private suspend fun fetchRatings(bookId: String): RatingSummary {
        // 4. CALL
        return httpClient.get("works/$bookId/ratings.json").body<BookRatingsResponse>().summary
    }

    // Needed to get page count and publish year...
    private suspend fun fetchEditions(bookId: String): List<BookEditionsEntry> {
        // 5. CALL
        return httpClient.get("works/$bookId/editions.json") {
            parameter("limit", 1)
        }.body<BookEditionsResponse>().entries
    }

}

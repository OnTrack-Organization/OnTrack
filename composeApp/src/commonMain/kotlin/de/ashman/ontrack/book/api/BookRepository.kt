package de.ashman.ontrack.book.api

import de.ashman.ontrack.book.model.Book
import de.ashman.ontrack.book.model.BookResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BookRepository(
    private val httpClient: HttpClient,
) {
    suspend fun fetchPopular(): Flow<List<Book>?> {
        return flow {
            val response: BookResponseDto = httpClient.get("subjects/love.json").body()
            val books = response.books?.map { it.toDomain() }

            emit(books)
        }
    }
}
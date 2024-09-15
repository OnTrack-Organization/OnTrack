package de.ashman.ontrack.media.show.api

import de.ashman.ontrack.media.show.model.Show
import de.ashman.ontrack.media.show.model.ShowResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ShowRepository(
    private val httpClient: HttpClient,
) {
    suspend fun fetchPopular(): Flow<List<Show>?> {
        return flow {
            val response: ShowResponseDto = httpClient.get("tv/popular").body()
            val movies = response.shows?.map { it.toDomain() }

            emit(movies)
        }
    }
}
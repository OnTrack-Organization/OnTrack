package de.ashman.ontrack.videogame

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VideoGameRepository(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager,
) {
    private suspend fun buildRequestWithToken(
        requestBuilder: HttpRequestBuilder.() -> Unit
    ): HttpRequestBuilder {
        val token = tokenManager.getAccessToken()
        return HttpRequestBuilder().apply {
            header(HttpHeaders.Authorization, "Bearer $token")
            requestBuilder()
        }
    }

    suspend fun fetchGames(): Flow<List<VideoGame>?> {
        return flow {
            val requestBuilder = buildRequestWithToken {
                url("games")
                // Some APIS need field selection to reduce traffic
                parameter("fields", "id,name,release_dates,summary,cover,genres,platforms,screenshots")
            }

            val response: List<VideoGameDto> = httpClient.request(requestBuilder).body()
            println(response)
            val videoGames = response.map { it.toDomain() }

            emit(videoGames)
        }
    }
}

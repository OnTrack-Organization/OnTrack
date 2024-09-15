package de.ashman.ontrack.media.music

import de.ashman.ontrack.auth.AccessTokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MusicRepository(
    private val httpClient: HttpClient,
    private val accessTokenManager: AccessTokenManager,
) {
    private suspend fun buildRequestWithToken(
        requestBuilder: HttpRequestBuilder.() -> Unit
    ): HttpRequestBuilder {
        val token = accessTokenManager.getAccessToken()
        return HttpRequestBuilder().apply {
            header(HttpHeaders.Authorization, "Bearer $token")
            requestBuilder()
        }
    }

    suspend fun fetchArtist(): Flow<List<Artist>?> {
        return flow {
            val requestBuilder = buildRequestWithToken {
                // TODO replace
                url("artists/4Z8W4fKeB5YxbusRsdQVPb")
            }

            val response: ArtistDto = httpClient.request(requestBuilder).body()

            val artist = response

            emit(listOf(artist.toDomain()))
        }
    }
}

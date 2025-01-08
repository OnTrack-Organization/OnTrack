package de.ashman.ontrack.api.album

import de.ashman.ontrack.auth.AccessTokenManager
import de.ashman.ontrack.media.model.Album
import de.ashman.ontrack.api.album.dto.AlbumDto
import de.ashman.ontrack.api.album.dto.AlbumSearchResult
import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders

class AlbumRepository(
    private val httpClient: HttpClient,
    private val accessTokenManager: AccessTokenManager,
) : MediaRepository<Album> {
    private suspend fun buildRequestWithToken(
        requestBuilder: HttpRequestBuilder.() -> Unit
    ): HttpRequestBuilder {
        val token = accessTokenManager.getAccessToken()
        return HttpRequestBuilder().apply {
            header(HttpHeaders.Authorization, "Bearer $token")
            requestBuilder()
        }
    }

    override suspend fun fetchMediaByQuery(query: String): Result<List<Album>> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("search")
                parameter("q", query)
                parameter("type", "album")
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }
            val response: AlbumSearchResult = httpClient.request(requestBuilder).body()

            response.albums.toDomain()
        }
    }

    override suspend fun fetchMediaDetails(id: String): Result<Album> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("albums/$id")
            }
            val response: AlbumDto = httpClient.request(requestBuilder).body()

            response.toDomain()
        }
    }
}

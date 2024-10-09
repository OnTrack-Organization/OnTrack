package de.ashman.ontrack.media.album.api

import de.ashman.ontrack.auth.AccessTokenManager
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.album.model.domain.Album
import de.ashman.ontrack.media.album.model.dto.AlbumDto
import de.ashman.ontrack.media.album.model.dto.AlbumSearchResult
import de.ashman.ontrack.media.MediaRepository
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

    override suspend fun fetchMediaByQuery(query: String): List<Album> {
        val requestBuilder = buildRequestWithToken {
            url("search")
            parameter("q", query)
            parameter("type", "album")
            parameter("limit", "10")
        }
        val response: AlbumSearchResult = httpClient.request(requestBuilder).body()

        val albums = response.albums.toDomain()

        return albums
    }

    override suspend fun fetchMediaDetails(id: String): Album {
        val requestBuilder = buildRequestWithToken {
            url("albums/$id")
        }
        val response: AlbumDto = httpClient.request(requestBuilder).body()

        val album = response.toDomain()

        return album
    }
}

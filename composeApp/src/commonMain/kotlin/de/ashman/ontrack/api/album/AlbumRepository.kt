package de.ashman.ontrack.api.album

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.album.dto.AlbumDto
import de.ashman.ontrack.api.album.dto.AlbumResponseDto
import de.ashman.ontrack.api.album.dto.AlbumSearchResult
import de.ashman.ontrack.api.album.dto.ArtistDto
import de.ashman.ontrack.api.auth.AccessTokenManager
import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.media.Album
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class AlbumRepository(
    private val httpClient: HttpClient,
    private val accessTokenManager: AccessTokenManager,
) : MediaRepository {
    private suspend inline fun <reified T> requestWithToken(url: String, block: HttpRequestBuilder.() -> Unit = {}): T {
        val token = accessTokenManager.getAccessToken(client = "Spotify")
        return httpClient.request {
            url(url)
            header(HttpHeaders.Authorization, "Bearer $token")
            apply(block)
        }.body()
    }

    override suspend fun fetchTrending(): Result<List<Album>> = safeApiCall {
        requestWithToken<AlbumSearchResult>("browse/new-releases") {
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.albums.toDomain()
    }

    override suspend fun fetchByQuery(query: String): Result<List<Album>> = safeApiCall {
        requestWithToken<AlbumSearchResult>("search") {
            parameter("q", query)
            parameter("type", "album")
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.albums.toDomain()
    }

    override suspend fun fetchDetails(mediaId: String): Result<Album> = safeApiCall {
        val albumDto = requestWithToken<AlbumDto>("albums/$mediaId")
        val album = albumDto.toDomain()

        val artist = album.mainArtist?.let { artist ->
            coroutineScope {
                val artistDeferred = async { requestWithToken<ArtistDto>("artists/${artist.id}").toDomain() }
                val albumsDeferred = async { requestWithToken<AlbumResponseDto>("artists/${artist.id}/albums").toDomain() }

                val artistInfo = artistDeferred.await()
                val artistAlbums = albumsDeferred.await()

                artistInfo.copy(artistAlbums = artistAlbums)
            }
        }

        // Return the album with the fetched artist details
        album.copy(mainArtist = artist)
    }
}

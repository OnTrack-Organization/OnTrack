package de.ashman.ontrack.api.album

import de.ashman.ontrack.api.MediaRepository
import de.ashman.ontrack.api.album.dto.AlbumDto
import de.ashman.ontrack.api.album.dto.AlbumResponseDto
import de.ashman.ontrack.api.album.dto.AlbumSearchResult
import de.ashman.ontrack.api.album.dto.ArtistDto
import de.ashman.ontrack.api.auth.AccessTokenManager
import de.ashman.ontrack.api.safeApiCall
import de.ashman.ontrack.di.DEFAULT_FETCH_LIMIT
import de.ashman.ontrack.domain.Album
import de.ashman.ontrack.domain.Artist
import de.ashman.ontrack.domain.Media
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
) : MediaRepository {
    private suspend fun buildRequestWithToken(
        requestBuilder: HttpRequestBuilder.() -> Unit
    ): HttpRequestBuilder {
        val token = accessTokenManager.getAccessToken(client = "Spotify")
        return HttpRequestBuilder().apply {
            header(HttpHeaders.Authorization, "Bearer $token")
            requestBuilder()
        }
    }

    override suspend fun fetchByQuery(query: String): Result<List<Album>> {
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

    override suspend fun fetchDetails(media: Media): Result<Album> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("albums/${media.id}")
            }
            val response: AlbumDto = httpClient.request(requestBuilder).body()
            val album = response.toDomain()

            val artistAndAlbums = fetchArtistAndAlbums(album.mainArtist.id).getOrNull()

            album.copy(mainArtist = artistAndAlbums ?: album.mainArtist)
        }
    }

    suspend fun fetchArtistAndAlbums(artistId: String): Result<Artist> {
        return safeApiCall {
            val artist = fetchArtist(artistId).getOrNull()
            val albums = fetchArtistAlbums(artistId).getOrNull()

            artist?.copy(artistAlbums = albums) ?: throw Exception("Artist not found")
        }
    }

    suspend fun fetchArtistAlbums(artistId: String): Result<List<Album>> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("artists/$artistId/albums")
            }
            val response: AlbumResponseDto = httpClient.request(requestBuilder).body()

            response.toDomain()
        }
    }

    suspend fun fetchArtist(artistId: String): Result<Artist> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("artists/$artistId")
            }
            val response: ArtistDto = httpClient.request(requestBuilder).body()

            response.toDomain()
        }
    }

    // Maybe just create a list of artists manually and take their newest albums
    override suspend fun fetchTrending(): Result<List<Album>> {
        return safeApiCall {
            val requestBuilder = buildRequestWithToken {
                url("browse/new-releases")
                parameter("limit", DEFAULT_FETCH_LIMIT)
            }

            val response: AlbumSearchResult = httpClient.request(requestBuilder).body()
            response.albums.toDomain()
        }
    }
}

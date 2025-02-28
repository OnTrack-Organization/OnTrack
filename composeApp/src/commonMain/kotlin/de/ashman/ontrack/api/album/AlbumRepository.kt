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
import de.ashman.ontrack.domain.media.Artist
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
    private suspend inline fun <reified T> requestWithToken(url: String, block: HttpRequestBuilder.() -> Unit = {}): T {
        val token = accessTokenManager.getAccessToken(client = "Spotify")
        return httpClient.request {
            url(url)
            header(HttpHeaders.Authorization, "Bearer $token")
            apply(block)
        }.body()
    }

    override suspend fun fetchByQuery(query: String): Result<List<Album>> = safeApiCall {
        requestWithToken<AlbumSearchResult>("search") {
            parameter("q", query)
            parameter("type", "album")
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.albums.toDomain()
    }

    override suspend fun fetchDetails(mediaId: String): Result<Album> = safeApiCall {
        val album = requestWithToken<AlbumDto>("albums/${mediaId}").toDomain()
        album.copy(mainArtist = fetchArtist(album.mainArtist?.id))
    }

    // Maybe just create a list of artists manually and take their newest albums
    override suspend fun fetchTrending(): Result<List<Album>> = safeApiCall {
        requestWithToken<AlbumSearchResult>("browse/new-releases") {
            parameter("limit", DEFAULT_FETCH_LIMIT)
        }.albums.toDomain()
    }

    private suspend fun fetchArtist(artistId: String?): Artist {
        val artist = requestWithToken<ArtistDto>("artists/$artistId").toDomain()
        val albums = requestWithToken<AlbumResponseDto>("artists/$artistId/albums").toDomain()
        return artist.copy(artistAlbums = albums)
    }
}

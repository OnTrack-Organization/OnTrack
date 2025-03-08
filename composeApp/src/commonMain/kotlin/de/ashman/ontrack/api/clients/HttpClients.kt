package de.ashman.ontrack.api.clients

import de.ashman.ontrack.BuildKonfig
import de.ashman.ontrack.di.BGG_PATH
import de.ashman.ontrack.di.BGG_URL
import de.ashman.ontrack.di.IGDB_PATH
import de.ashman.ontrack.di.IGDB_URL
import de.ashman.ontrack.di.OPEN_LIB_URL
import de.ashman.ontrack.di.SPOTIFY_PATH
import de.ashman.ontrack.di.SPOTIFY_TOKEN_PATH
import de.ashman.ontrack.di.SPOTIFY_TOKEN_URL
import de.ashman.ontrack.di.SPOTIFY_URL
import de.ashman.ontrack.di.TMDB_PATH_URL
import de.ashman.ontrack.di.TMDB_URL
import de.ashman.ontrack.di.TWITCH_TOKEN_PATH
import de.ashman.ontrack.di.TWITCH_TOKEN_URL
import io.ktor.client.HttpClient
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path

fun createTMDBClient(): HttpClient = HttpClient {
    setupContentNegotiation()

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = TMDB_URL
            path(TMDB_PATH_URL)
            parameters.append("api_key", BuildKonfig.TMDB_API_KEY)
        }
    }
}

fun createOpenLibraryClient(): HttpClient = HttpClient {
    setupContentNegotiation()

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = OPEN_LIB_URL
        }
    }

    install(UserAgent) {
        agent = "OnTrack/1.0 (ashkan.haghighifashi@gmail.com)"
    }
}

fun createBGGClient(): HttpClient = HttpClient {
    setupContentNegotiation()

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = BGG_URL
            path(BGG_PATH)
        }
    }
}

fun createIGDBClient(): HttpClient = HttpClient {
    setupContentNegotiation()

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = IGDB_URL
            path(IGDB_PATH)
        }
        headers {
            append("Client-ID", BuildKonfig.TWITCH_CLIENT_ID)
        }
    }
}

fun createSpotifyClient(): HttpClient = HttpClient {
    setupContentNegotiation()

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = SPOTIFY_URL
            path(SPOTIFY_PATH)
            contentType(ContentType.Application.FormUrlEncoded)
        }
    }
}

fun createSpotifyTokenClient(): HttpClient = HttpClient {
    setupContentNegotiation()

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = SPOTIFY_TOKEN_URL
            path(SPOTIFY_TOKEN_PATH)
            contentType(ContentType.Application.FormUrlEncoded)
        }
    }
}

fun createTwitchTokenClient(): HttpClient = HttpClient {
    setupContentNegotiation()

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = TWITCH_TOKEN_URL
            path(TWITCH_TOKEN_PATH)
        }
    }
}

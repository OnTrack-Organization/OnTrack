package de.ashman.ontrack.network.clients

import de.ashman.ontrack.BuildKonfig
import de.ashman.ontrack.di.BACKEND_HOST
import de.ashman.ontrack.di.BACKEND_PORT
import de.ashman.ontrack.di.BGG_PATH
import de.ashman.ontrack.di.BGG_URL
import de.ashman.ontrack.di.GEEKDO_PATH
import de.ashman.ontrack.di.GEEKDO_URL
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
import dev.gitlive.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
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
            parameters.append("include_adult", "false")
        }
    }
}

fun createOpenLibraryClient(): HttpClient = HttpClient {
    setupContentNegotiation()

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = OPEN_LIB_URL

            parameters.append("lang", "eng")
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

fun createGeekDoClient(): HttpClient = HttpClient {
    setupContentNegotiation()

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = GEEKDO_URL
            path(GEEKDO_PATH)
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

fun createBackendClient(auth: FirebaseAuth): HttpClient = HttpClient {
    setupBackendContentNegotiation()

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = BACKEND_HOST
            port = BACKEND_PORT
        }
    }

    install(Auth) {
        bearer {
            loadTokens {
                val token = auth.currentUser?.getIdToken(true) ?: return@loadTokens null
                BearerTokens(accessToken = token, refreshToken = null)
            }
            // Backend needs to send 401 and WWW-Authenticate header to trigger refresh
            refreshTokens {
                val token = auth.currentUser?.getIdToken(true) ?: return@refreshTokens null
                BearerTokens(accessToken = token, refreshToken = null)
            }
        }
    }
}

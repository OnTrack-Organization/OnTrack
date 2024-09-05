package de.ashman.ontrack.di

import de.ashman.ontrack.BuildKonfig
import de.ashman.ontrack.boardgame.api.BoardGameRepository
import de.ashman.ontrack.boardgame.ui.BoardGameViewModel
import de.ashman.ontrack.book.api.BookRepository
import de.ashman.ontrack.show.api.ShowRepository
import de.ashman.ontrack.book.ui.BookViewModel
import de.ashman.ontrack.movie.api.MovieRepository
import de.ashman.ontrack.movie.ui.MovieViewModel
import de.ashman.ontrack.show.ui.ShowViewModel
import de.ashman.ontrack.videogame.TokenManager
import de.ashman.ontrack.videogame.VideoGameRepository
import de.ashman.ontrack.videogame.VideoGameViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.headers
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// MOVIES AND TV SHOWS
const val TMDB_CLIENT_NAME = "TMDBClient"
const val TMDB_URL = "api.themoviedb.org"
const val TMDB_PATH_URL = "3/"

// BOOKS
const val OPEN_LIB_CLIENT_NAME = "OpenLibraryClient"
const val OPEN_LIB_URL = "openlibrary.org"

// BOARD GAMES
const val BGG_CLIENT_NAME = "BoardGameGeekClient"
const val BGG_URL = "boardgamegeek.com"
const val BGG_PATH = "xmlapi2/"

// VIDEO GAMES
const val IGDB_CLIENT_NAME = "IGDBClient"
const val IGDB_URL = "api.igdb.com"
const val IGDB_PATH = "v4/"

const val TWITCH_TOKEN_CLIENT_NAME = "TwitchTokenClient"
const val TWITCH_TOKEN_URL = "id.twitch.tv"
const val TWITCH_TOKEN_PATH = "oauth2/token"

@OptIn(ExperimentalSerializationApi::class)
val appModule =
    module {
        // Define your shared dependencies here
        single(named(TMDB_CLIENT_NAME)) {
            HttpClient {
                defaultRequest {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = TMDB_URL
                        path(TMDB_PATH_URL)
                        parameters.append("api_key", BuildKonfig.TMDB_API_KEY)
                    }
                }

                install(ContentNegotiation) {
                    json(
                        Json {
                            namingStrategy = JsonNamingStrategy.SnakeCase
                            ignoreUnknownKeys = true
                            isLenient = true
                        }
                    )
                }
            }
        }

        single(named(OPEN_LIB_CLIENT_NAME)) {
            HttpClient {
                defaultRequest {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = OPEN_LIB_URL
                    }
                }

                install(ContentNegotiation) {
                    json(
                        Json {
                            namingStrategy = JsonNamingStrategy.SnakeCase
                            ignoreUnknownKeys = true
                            isLenient = true
                        }
                    )
                }
            }
        }

        single(named(BGG_CLIENT_NAME)) {
            HttpClient {
                defaultRequest {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = BGG_URL
                        path(BGG_PATH)
                    }
                }

                install(ContentNegotiation) {
                    json(
                        Json {
                            namingStrategy = JsonNamingStrategy.SnakeCase
                            ignoreUnknownKeys = true
                            isLenient = true
                        }
                    )
                }
            }
        }

        single(named(IGDB_CLIENT_NAME)) {
            HttpClient {
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

                install(ContentNegotiation) {
                    json(
                        Json {
                            namingStrategy = JsonNamingStrategy.SnakeCase
                            ignoreUnknownKeys = true
                            isLenient = true
                        }
                    )
                }
            }
        }
        single(named(TWITCH_TOKEN_CLIENT_NAME)) {
            HttpClient {
                defaultRequest {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = TWITCH_TOKEN_URL
                        path(TWITCH_TOKEN_PATH)
                    }
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            namingStrategy = JsonNamingStrategy.SnakeCase
                            ignoreUnknownKeys = true
                            isLenient = true
                        }
                    )
                }
            }
        }

        single { MovieRepository(get(named(TMDB_CLIENT_NAME))) }
        single { ShowRepository(get(named(TMDB_CLIENT_NAME))) }
        single { BookRepository(get(named(OPEN_LIB_CLIENT_NAME))) }
        single { BoardGameRepository(get(named(BGG_CLIENT_NAME))) }

        single { TokenManager(get(named(TWITCH_TOKEN_CLIENT_NAME)), BuildKonfig.TWITCH_CLIENT_ID, BuildKonfig.TWITCH_CLIENT_SECRET) }
        single { VideoGameRepository(get(named(IGDB_CLIENT_NAME)), get()) }

        viewModelDefinition { MovieViewModel(get()) }
        viewModelDefinition { ShowViewModel(get()) }
        viewModelDefinition { BookViewModel(get()) }
        viewModelDefinition { VideoGameViewModel(get()) }
        viewModelDefinition { BoardGameViewModel(get()) }
    }

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
        )
    }

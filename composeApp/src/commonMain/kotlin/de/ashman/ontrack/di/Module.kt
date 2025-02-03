package de.ashman.ontrack.di

import de.ashman.ontrack.BuildKonfig
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.api.auth.AccessTokenManager
import de.ashman.ontrack.api.boardgame.BoardgameRepository
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.api.movie.MovieRepository
import de.ashman.ontrack.api.show.ShowRepository
import de.ashman.ontrack.api.videogame.VideogameRepository
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.authentication.AuthServiceImpl
import de.ashman.ontrack.authentication.AuthViewModel
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.features.shelf.ShelfViewModel
import de.ashman.ontrack.features.shelflist.ShelfListViewModel
import de.ashman.ontrack.db.FirestoreService
import de.ashman.ontrack.db.FirestoreServiceImpl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
val appModule = module {
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

            install(UserAgent) {
                agent = "OnTrackApp/1.0 (ashkan.haghighifashi@gmail.com)"
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

    single(named(SPOTIFY_CLIENT_NAME)) {
        HttpClient {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = SPOTIFY_URL
                    path(SPOTIFY_PATH)
                    contentType(ContentType.Application.FormUrlEncoded)
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
    single(named(SPOTIFY_TOKEN_CLIENT_NAME)) {
        HttpClient {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = SPOTIFY_TOKEN_URL
                    path(SPOTIFY_TOKEN_PATH)
                    contentType(ContentType.Application.FormUrlEncoded)
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

    // SERVICES
    single<AuthService> { AuthServiceImpl() }
    single(named(TWITCH_TOKEN_CLIENT_NAME)) { AccessTokenManager(get(named(TWITCH_TOKEN_CLIENT_NAME)), BuildKonfig.TWITCH_CLIENT_ID, BuildKonfig.TWITCH_CLIENT_SECRET) }
    single(named(SPOTIFY_TOKEN_CLIENT_NAME)) { AccessTokenManager(get(named(SPOTIFY_TOKEN_CLIENT_NAME)), BuildKonfig.SPOTIFY_CLIENT_ID, BuildKonfig.SPOTIFY_CLIENT_SECRET) }

    // REPOSITORY
    single { MovieRepository(get(named(TMDB_CLIENT_NAME))) }
    single { ShowRepository(get(named(TMDB_CLIENT_NAME))) }
    single { BookRepository(get(named(OPEN_LIB_CLIENT_NAME))) }
    single { BoardgameRepository(get(named(BGG_CLIENT_NAME))) }
    single { VideogameRepository(get(named(IGDB_CLIENT_NAME)), get(named(TWITCH_TOKEN_CLIENT_NAME))) }
    single { AlbumRepository(get(named(SPOTIFY_CLIENT_NAME)), get(named(SPOTIFY_TOKEN_CLIENT_NAME))) }

    // VIEWMODEL
    viewModelDefinition { AuthViewModel(get()) }
    viewModelDefinition { SearchViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModelDefinition { DetailViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModelDefinition { ShelfViewModel(get(), get()) }
    viewModelDefinition { ShelfListViewModel(get()) }

    // TEST
    single<FirestoreService> { FirestoreServiceImpl(Firebase.firestore) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
        )
    }

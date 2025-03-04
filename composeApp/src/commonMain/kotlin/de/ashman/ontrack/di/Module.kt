package de.ashman.ontrack.di

import de.ashman.ontrack.BuildKonfig
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.api.auth.AccessTokenManager
import de.ashman.ontrack.api.boardgame.BoardgameRepository
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.api.movie.MovieRepository
import de.ashman.ontrack.api.show.ShowRepository
import de.ashman.ontrack.api.videogame.VideogameRepository
import de.ashman.ontrack.db.AuthRepository
import de.ashman.ontrack.db.AuthRepositoryImpl
import de.ashman.ontrack.db.FeedRepository
import de.ashman.ontrack.db.FeedRepositoryImpl
import de.ashman.ontrack.db.FriendRepository
import de.ashman.ontrack.db.FriendRepositoryImpl
import de.ashman.ontrack.db.RecommendationRepository
import de.ashman.ontrack.db.RecommendationRepositoryImpl
import de.ashman.ontrack.db.TrackingRepository
import de.ashman.ontrack.db.TrackingRepositoryImpl
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.feed.FeedViewModel
import de.ashman.ontrack.features.feed.friend.FriendsViewModel
import de.ashman.ontrack.features.init.login.LoginViewModel
import de.ashman.ontrack.features.init.start.StartViewModel
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.features.settings.SettingsViewModel
import de.ashman.ontrack.features.shelf.ShelfViewModel
import de.ashman.ontrack.features.shelflist.ShelfListViewModel
import de.ashman.ontrack.notification.NotificationService
import de.ashman.ontrack.notification.NotificationServiceImpl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import dev.gitlive.firebase.storage.storage
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
                agent = "OnTrack/1.0 (ashkan.haghighifashi@gmail.com)"
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

    // ANALYTICS
    single { Firebase.analytics }

    // AUTH
    single { Firebase.auth }
    single(named(TWITCH_TOKEN_CLIENT_NAME)) { AccessTokenManager(get(named(TWITCH_TOKEN_CLIENT_NAME)), BuildKonfig.TWITCH_CLIENT_ID, BuildKonfig.TWITCH_CLIENT_SECRET) }
    single(named(SPOTIFY_TOKEN_CLIENT_NAME)) { AccessTokenManager(get(named(SPOTIFY_TOKEN_CLIENT_NAME)), BuildKonfig.SPOTIFY_CLIENT_ID, BuildKonfig.SPOTIFY_CLIENT_SECRET) }

    // NOTIFICATIONS
    single { Firebase.functions }
    single<NotificationService> { NotificationServiceImpl(get(), get()) }

    // DATABASE
    single { Firebase.firestore }
    single { Firebase.storage }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<FriendRepository> { FriendRepositoryImpl(get(), get()) }
    single<FeedRepository> { FeedRepositoryImpl(get(), get()) }
    single<TrackingRepository> { TrackingRepositoryImpl(get(), get()) }
    single<RecommendationRepository> { RecommendationRepositoryImpl(get(), get()) }

    // API
    single { MovieRepository(get(named(TMDB_CLIENT_NAME))) }
    single { ShowRepository(get(named(TMDB_CLIENT_NAME))) }
    single { BookRepository(get(named(OPEN_LIB_CLIENT_NAME))) }
    single { BoardgameRepository(get(named(BGG_CLIENT_NAME))) }
    single { VideogameRepository(get(named(IGDB_CLIENT_NAME)), get(named(TWITCH_TOKEN_CLIENT_NAME))) }
    single { AlbumRepository(get(named(SPOTIFY_CLIENT_NAME)), get(named(SPOTIFY_TOKEN_CLIENT_NAME))) }

    // VIEWMODEL
    viewModelDefinition { StartViewModel() }
    viewModelDefinition { LoginViewModel(get()) }
    viewModelDefinition { FeedViewModel(get(), get()) }
    viewModelDefinition { FriendsViewModel(get(), get(), get()) }
    viewModelDefinition { SearchViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModelDefinition { DetailViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModelDefinition { ShelfViewModel(get(), get()) }
    viewModelDefinition { ShelfListViewModel(get()) }
    viewModelDefinition { SettingsViewModel(get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
        )
    }

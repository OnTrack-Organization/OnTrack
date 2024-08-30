package de.ashman.ontrack.di

import de.ashman.ontrack.BuildKonfig
import de.ashman.ontrack.book.api.BookRepository
import de.ashman.ontrack.show.api.ShowRepository
import de.ashman.ontrack.book.ui.BookViewModel
import de.ashman.ontrack.movie.api.MovieRepository
import de.ashman.ontrack.movie.ui.MovieViewModel
import de.ashman.ontrack.show.ui.ShowViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// MOVIES AND TV SHOWS
const val TMDB_URL = "api.themoviedb.org"
const val TMDB_PATH_URL = "3/"

// BOOKS
const val OPEN_LIB_URL = "openlibrary.org"

val appModule =
    module {
        // Define your shared dependencies here
        single(named("TMDBClient")) {
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
                            ignoreUnknownKeys = true
                            isLenient = true
                        }
                    )
                }
            }
        }
        single(named("OpenLibraryClient")) {
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
                            ignoreUnknownKeys = true
                            isLenient = true
                        }
                    )
                }
            }
        }

        single { MovieRepository(get(named("TMDBClient"))) }
        single { ShowRepository(get(named("TMDBClient"))) }

        single { BookRepository(get(named("OpenLibraryClient"))) }

        viewModelDefinition { MovieViewModel(get()) }
        viewModelDefinition { ShowViewModel(get()) }
        viewModelDefinition { BookViewModel(get()) }
    }

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
        )
    }

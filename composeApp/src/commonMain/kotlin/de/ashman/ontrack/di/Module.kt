package de.ashman.ontrack.di

import de.ashman.ontrack.BuildKonfig
import de.ashman.ontrack.movie.api.MovieRepository
import de.ashman.ontrack.show.api.ShowRepository
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

const val BASE_URL = "api.themoviedb.org"
const val PATH_URL = "3/"

val appModule =
    module {
        // Define your shared dependencies here
        single(named("TMDBClient")) {
            HttpClient {
                defaultRequest {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = BASE_URL
                        path(PATH_URL)
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
// TODO api key einbauen
            }
        }
        single { MovieRepository(get(named("TMDBClient"))) }
        single { ShowRepository(get(named("TMDBClient"))) }

        viewModelDefinition { MovieViewModel(get()) }
        viewModelDefinition { ShowViewModel(get()) }
    }

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
        )
    }

package de.ashman.ontrack.di

import de.ashman.ontrack.BuildKonfig
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.api.auth.AccessTokenManager
import de.ashman.ontrack.api.boardgame.BoardgameRepository
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.api.clients.createBGGClient
import de.ashman.ontrack.api.clients.createIGDBClient
import de.ashman.ontrack.api.clients.createOpenLibraryClient
import de.ashman.ontrack.api.clients.createSpotifyClient
import de.ashman.ontrack.api.clients.createSpotifyTokenClient
import de.ashman.ontrack.api.clients.createTMDBClient
import de.ashman.ontrack.api.clients.createTwitchTokenClient
import de.ashman.ontrack.api.movie.MovieRepository
import de.ashman.ontrack.api.show.ShowRepository
import de.ashman.ontrack.api.videogame.VideogameRepository
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.detail.recommendation.RecommendationViewModel
import de.ashman.ontrack.features.feed.FeedViewModel
import de.ashman.ontrack.features.feed.friend.FriendsViewModel
import de.ashman.ontrack.features.init.login.LoginViewModel
import de.ashman.ontrack.features.init.setup.SetupViewModel
import de.ashman.ontrack.features.init.start.StartViewModel
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.features.settings.SettingsViewModel
import de.ashman.ontrack.features.shelf.ShelfViewModel
import de.ashman.ontrack.features.shelflist.ShelfListViewModel
import de.ashman.ontrack.features.usecase.AcceptRequestUseCase
import de.ashman.ontrack.features.usecase.CancelRequestUseCase
import de.ashman.ontrack.features.usecase.DeclineRequestUseCase
import de.ashman.ontrack.features.usecase.RemoveFriendUseCase
import de.ashman.ontrack.features.usecase.SendRequestUseCase
import de.ashman.ontrack.features.usecase.UsernameValidationUseCase
import de.ashman.ontrack.notification.NotificationService
import de.ashman.ontrack.notification.NotificationServiceImpl
import de.ashman.ontrack.repository.CurrentUserRepository
import de.ashman.ontrack.repository.CurrentUserRepositoryImpl
import de.ashman.ontrack.repository.SelectedMediaRepository
import de.ashman.ontrack.repository.SelectedMediaRepositoryImpl
import de.ashman.ontrack.repository.firestore.FeedRepository
import de.ashman.ontrack.repository.firestore.FeedRepositoryImpl
import de.ashman.ontrack.repository.firestore.FirestoreUserRepository
import de.ashman.ontrack.repository.firestore.FirestoreUserRepositoryImpl
import de.ashman.ontrack.repository.firestore.FriendRepository
import de.ashman.ontrack.repository.firestore.FriendRepositoryImpl
import de.ashman.ontrack.repository.firestore.RecommendationRepository
import de.ashman.ontrack.repository.firestore.RecommendationRepositoryImpl
import de.ashman.ontrack.repository.firestore.TrackingRepository
import de.ashman.ontrack.repository.firestore.TrackingRepositoryImpl
import de.ashman.ontrack.storage.StorageRepository
import de.ashman.ontrack.storage.StorageRepositoryImpl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import dev.gitlive.firebase.storage.storage
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    // API
    single(named(TMDB_CLIENT_NAME)) { createTMDBClient() }
    single(named(OPEN_LIB_CLIENT_NAME)) { createOpenLibraryClient() }
    single(named(BGG_CLIENT_NAME)) { createBGGClient() }
    single(named(IGDB_CLIENT_NAME)) { createIGDBClient() }
    single(named(SPOTIFY_CLIENT_NAME)) { createSpotifyClient() }
    single(named(SPOTIFY_TOKEN_CLIENT_NAME)) { createSpotifyTokenClient() }
    single(named(TWITCH_TOKEN_CLIENT_NAME)) { createTwitchTokenClient() }

    // API
    single { MovieRepository(get(named(TMDB_CLIENT_NAME))) }
    single { ShowRepository(get(named(TMDB_CLIENT_NAME))) }
    single { BookRepository(get(named(OPEN_LIB_CLIENT_NAME))) }
    single { BoardgameRepository(get(named(BGG_CLIENT_NAME))) }
    single { VideogameRepository(get(named(IGDB_CLIENT_NAME)), get(named(TWITCH_TOKEN_CLIENT_NAME))) }
    single { AlbumRepository(get(named(SPOTIFY_CLIENT_NAME)), get(named(SPOTIFY_TOKEN_CLIENT_NAME))) }

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

    single<FirestoreUserRepository> { FirestoreUserRepositoryImpl(get(), get(), get()) }
    single<CurrentUserRepository> { CurrentUserRepositoryImpl() }
    single<FriendRepository> { FriendRepositoryImpl(get(), get()) }
    single<FeedRepository> { FeedRepositoryImpl(get()) }
    single<TrackingRepository> { TrackingRepositoryImpl(get(), get()) }
    single<RecommendationRepository> { RecommendationRepositoryImpl(get(), get()) }
    single<SelectedMediaRepository> { SelectedMediaRepositoryImpl() }
    single<StorageRepository> { StorageRepositoryImpl(get(), get()) }

    single { SharedUiManager() }

    // USE CASES
    singleOf(::UsernameValidationUseCase)
    singleOf(::SendRequestUseCase)
    singleOf(::CancelRequestUseCase)
    singleOf(::AcceptRequestUseCase)
    singleOf(::DeclineRequestUseCase)
    singleOf(::RemoveFriendUseCase)

    // VIEWMODEL
    viewModelDefinition { StartViewModel() }
    viewModelDefinition { LoginViewModel(get(), get(), get()) }
    viewModelDefinition { FeedViewModel(get(), get(), get(), get(), get(), get()) }
    viewModelDefinition { FriendsViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModelDefinition { SearchViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModelDefinition { DetailViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModelDefinition { RecommendationViewModel(get(), get(), get(), get(), get()) }
    viewModelDefinition { ShelfViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModelDefinition { ShelfListViewModel(get()) }
    viewModelDefinition { SettingsViewModel(get(), get(), get(), get(), get()) }
    viewModelDefinition { SetupViewModel(get(), get(), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
        )
    }

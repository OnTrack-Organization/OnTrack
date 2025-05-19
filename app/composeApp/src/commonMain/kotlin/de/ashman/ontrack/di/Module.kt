package de.ashman.ontrack.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import de.ashman.ontrack.BuildKonfig
import de.ashman.ontrack.api.album.AlbumRepository
import de.ashman.ontrack.api.auth.AccessTokenManager
import de.ashman.ontrack.api.boardgame.BoardgameRepository
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.api.movie.MovieRepository
import de.ashman.ontrack.api.show.ShowRepository
import de.ashman.ontrack.api.videogame.VideogameRepository
import de.ashman.ontrack.database.TrackingDao
import de.ashman.ontrack.database.TrackingDatabase
import de.ashman.ontrack.database.TrackingRepository
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.datastore.createDataStore
import de.ashman.ontrack.datastore.dataStoreFileName
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.friend.FriendsViewModel
import de.ashman.ontrack.features.init.setup.SetupViewModel
import de.ashman.ontrack.features.init.signin.LoginViewModel
import de.ashman.ontrack.features.init.start.StartViewModel
import de.ashman.ontrack.features.notifications.NotificationsViewModel
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.features.settings.SettingsViewModel
import de.ashman.ontrack.features.share.ShareViewModel
import de.ashman.ontrack.features.share_detail.ShareDetailViewModel
import de.ashman.ontrack.features.shelf.ShelfViewModel
import de.ashman.ontrack.features.shelflist.ShelfListViewModel
import de.ashman.ontrack.network.clients.createBGGClient
import de.ashman.ontrack.network.clients.createBackendClient
import de.ashman.ontrack.network.clients.createGeekDoClient
import de.ashman.ontrack.network.clients.createIGDBClient
import de.ashman.ontrack.network.clients.createOpenLibraryClient
import de.ashman.ontrack.network.clients.createSpotifyClient
import de.ashman.ontrack.network.clients.createSpotifyTokenClient
import de.ashman.ontrack.network.clients.createTMDBClient
import de.ashman.ontrack.network.clients.createTwitchTokenClient
import de.ashman.ontrack.network.services.account.AccountService
import de.ashman.ontrack.network.services.account.AccountServiceImpl
import de.ashman.ontrack.network.services.friend.FriendService
import de.ashman.ontrack.network.services.friend.FriendServiceImpl
import de.ashman.ontrack.network.services.recommendation.RecommendationService
import de.ashman.ontrack.network.services.recommendation.RecommendationServiceImpl
import de.ashman.ontrack.network.services.signin.SignInService
import de.ashman.ontrack.network.services.signin.SignInServiceImpl
import de.ashman.ontrack.network.services.tracking.TrackingService
import de.ashman.ontrack.network.services.tracking.TrackingServiceImpl
import de.ashman.ontrack.notification.NotificationService
import de.ashman.ontrack.notification.NotificationServiceImpl
import de.ashman.ontrack.repository.SelectedMediaRepository
import de.ashman.ontrack.repository.SelectedMediaRepositoryImpl
import de.ashman.ontrack.repository.firestore.FirebaseTrackingRepository
import de.ashman.ontrack.repository.firestore.FirebaseTrackingRepositoryImpl
import de.ashman.ontrack.repository.firestore.ShareRepository
import de.ashman.ontrack.repository.firestore.ShareRepositoryImpl
import de.ashman.ontrack.storage.StorageRepository
import de.ashman.ontrack.storage.StorageRepositoryImpl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import dev.gitlive.firebase.storage.storage
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    // API
    single(named(TMDB_CLIENT_NAME)) { createTMDBClient() }
    single(named(OPEN_LIB_CLIENT_NAME)) { createOpenLibraryClient() }
    single(named(BGG_CLIENT_NAME)) { createBGGClient() }
    single(named(GEEKDO_CLIENT_NAME)) { createGeekDoClient() }
    single(named(IGDB_CLIENT_NAME)) { createIGDBClient() }
    single(named(SPOTIFY_CLIENT_NAME)) { createSpotifyClient() }
    single(named(SPOTIFY_TOKEN_CLIENT_NAME)) { createSpotifyTokenClient() }
    single(named(TWITCH_TOKEN_CLIENT_NAME)) { createTwitchTokenClient() }
    single(named(BACKEND_CLIENT_NAME)) { createBackendClient(get()) }

    single { MovieRepository(get(named(TMDB_CLIENT_NAME))) }
    single { ShowRepository(get(named(TMDB_CLIENT_NAME))) }
    single { BookRepository(get(named(OPEN_LIB_CLIENT_NAME))) }
    single { BoardgameRepository(get(named(BGG_CLIENT_NAME)), get(named(GEEKDO_CLIENT_NAME))) }
    single { VideogameRepository(get(named(IGDB_CLIENT_NAME)), get(named(TWITCH_TOKEN_CLIENT_NAME))) }
    single { AlbumRepository(get(named(SPOTIFY_CLIENT_NAME)), get(named(SPOTIFY_TOKEN_CLIENT_NAME))) }

    // SERVICES
    single<SignInService> { SignInServiceImpl(get(named(BACKEND_CLIENT_NAME)), get()) }
    single<AccountService> { AccountServiceImpl(get(named(BACKEND_CLIENT_NAME))) }
    single<TrackingService> { TrackingServiceImpl(get(named(BACKEND_CLIENT_NAME))) }
    single<FriendService> { FriendServiceImpl(get(named(BACKEND_CLIENT_NAME))) }
    single<RecommendationService> { RecommendationServiceImpl(get(named(BACKEND_CLIENT_NAME))) }

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
    single<DataStore<Preferences>> { createDataStore { dataStoreFileName } }
    single { UserDataStore(get()) }

    single<TrackingDao> { get<TrackingDatabase>().getTrackingDao() }
    single<TrackingRepository> { TrackingRepository(get()) }

    single { Firebase.firestore }
    single { Firebase.storage }

    single<ShareRepository> { ShareRepositoryImpl(get()) }
    single<FirebaseTrackingRepository> { FirebaseTrackingRepositoryImpl(get(), get()) }
    single<SelectedMediaRepository> { SelectedMediaRepositoryImpl() }
    single<StorageRepository> { StorageRepositoryImpl(get()) }

    // UI
    single { CommonUiManager() }

    viewModelDefinition { StartViewModel() }
    viewModelDefinition { LoginViewModel(get(), get(), get(), get(), get()) }
    viewModelDefinition { SetupViewModel(get(), get(), get(), get()) }

    viewModelDefinition { ShareViewModel(get(), get(), get(), get(), get()) }
    viewModelDefinition { ShareDetailViewModel() }
    viewModelDefinition { FriendsViewModel(get(), get()) }
    viewModelDefinition { NotificationsViewModel() }

    viewModelDefinition { SearchViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModelDefinition { DetailViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    viewModelDefinition { ShelfViewModel(get(), get(), get(), get()) }
    viewModelDefinition { ShelfListViewModel(get()) }

    viewModelDefinition { SettingsViewModel(get(), get(), get(), get(), get(), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
            platformModule,
        )
    }

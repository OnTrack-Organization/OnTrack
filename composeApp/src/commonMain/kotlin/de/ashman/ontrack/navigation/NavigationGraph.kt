package de.ashman.ontrack.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.ashman.ontrack.OnTrackScreen
import de.ashman.ontrack.feed.FeedScreen
import de.ashman.ontrack.login.ui.LoginScreen
import de.ashman.ontrack.detail.MovieDetailScreen
import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.media.ui.detail.AlbumDetailScreen
import de.ashman.ontrack.media.ui.detail.BoardgameDetailScreen
import de.ashman.ontrack.media.ui.detail.BookDetailScreen
import de.ashman.ontrack.media.ui.detail.ShowDetailScreen
import de.ashman.ontrack.media.ui.detail.VideogameDetailScreen
import de.ashman.ontrack.search.SearchScreen
import de.ashman.ontrack.search.SearchViewModel
import de.ashman.ontrack.shelf.ShelfListScreen
import de.ashman.ontrack.shelf.ShelfScreen
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.koinInject

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    // TODO maybe put somewhere else, but for now it needs to be here so its not recreated on every navigation
    val searchViewModel: SearchViewModel = koinInject()

    OnTrackScreen(navController) { padding ->
        NavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize().padding(padding),
            startDestination = if (Firebase.auth.currentUser != null) Route.Search else Route.Login,
        ) {
            composable<Route.Login> {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(Route.Feed) }
                )
            }
            mainGraph(navController, searchViewModel)

            mediaGraph(navController)

            shelfGraph(navController)
        }
    }
}

fun NavGraphBuilder.mediaGraph(
    navController: NavHostController,
) {
    composable<Route.Movie> { backStackEntry ->
        val movie: Route.Movie = backStackEntry.toRoute()
        MovieDetailScreen(
            id = movie.id,
            onBack = { navController.popBackStack() }
        )
    }

    composable<Route.Show> { backStackEntry ->
        val show: Route.Show = backStackEntry.toRoute()
        ShowDetailScreen(
            id = show.id,
            onBack = { navController.popBackStack() }
        )
    }

    composable<Route.Videogame> { backStackEntry ->
        val videogame: Route.Videogame = backStackEntry.toRoute()
        VideogameDetailScreen(
            id = videogame.id,
            onBack = { navController.popBackStack() }
        )
    }

    composable<Route.Boardgame> { backStackEntry ->
        val boardgame: Route.Boardgame = backStackEntry.toRoute()
        BoardgameDetailScreen(
            id = boardgame.id,
            onBack = { navController.popBackStack() }
        )
    }

    composable<Route.Book> { backStackEntry ->
        val book: Route.Book = backStackEntry.toRoute()
        BookDetailScreen(
            id = book.id,
            onBack = { navController.popBackStack() }
        )
    }

    composable<Route.Album> { backStackEntry ->
        val album: Route.Album = backStackEntry.toRoute()
        AlbumDetailScreen(
            id = album.id,
            onBack = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.shelfGraph(
    navController: NavHostController,
) {
    composable<Route.ShelfList> { backStackEntry ->
        val shelfList: Route.ShelfList = backStackEntry.toRoute()

        val mediaTypeEnum = MediaType.valueOf(shelfList.mediaType.uppercase())
        //TODO die liste implementieren
        ShelfListScreen(mediaTypeEnum)
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    searchViewModel: SearchViewModel,
) {
    composable<Route.Feed> {
        FeedScreen()
    }

    composable<Route.Search> {
        SearchScreen(
            viewModel = searchViewModel,
            onClickItem = { item ->
                when (item.type) {
                    MediaType.MOVIE -> navController.navigate(Route.Movie(item.id))
                    MediaType.SHOW -> navController.navigate(Route.Show(item.id))
                    MediaType.BOOK -> navController.navigate(Route.Book(item.id))
                    MediaType.VIDEOGAME -> navController.navigate(Route.Videogame(item.id))
                    MediaType.BOARDGAME -> navController.navigate(Route.Boardgame(item.id))
                    MediaType.ALBUM -> navController.navigate(Route.Album(item.id))
                }
            }
        )
    }

    composable<Route.Shelf> {
        ShelfScreen(
            goToShelf = { mediaType -> navController.navigate(Route.ShelfList(mediaType.name)) }
        )
    }
}

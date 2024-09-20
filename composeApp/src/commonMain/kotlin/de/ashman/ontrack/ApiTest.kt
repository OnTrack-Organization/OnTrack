package de.ashman.ontrack

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import de.ashman.ontrack.media.boardgame.ui.BoardGameViewModel
import de.ashman.ontrack.media.book.ui.BookViewModel
import de.ashman.ontrack.login.ui.UserViewModel
import de.ashman.ontrack.media.movie.ui.MovieViewModel
import de.ashman.ontrack.media.music.MusicViewModel
import de.ashman.ontrack.media.show.ui.ShowViewModel
import de.ashman.ontrack.media.videogame.ui.VideoGameViewModel
import org.koin.compose.koinInject

@Composable
fun ApiTest(
    modifier: Modifier = Modifier,
    movieViewModel: MovieViewModel = koinInject(),
    showViewModel: ShowViewModel = koinInject(),
    bookViewModel: BookViewModel = koinInject(),
    videoGameViewModel: VideoGameViewModel = koinInject(),
    boardGameViewModel: BoardGameViewModel = koinInject(),
    musicViewModel: MusicViewModel = koinInject(),
    userViewModel: UserViewModel = koinInject(),
) {
    val movieState by movieViewModel.uiState.collectAsState()
    val showState by showViewModel.uiState.collectAsState()
    val bookState by bookViewModel.uiState.collectAsState()
    val gameState by videoGameViewModel.uiState.collectAsState()
    val bgState by boardGameViewModel.uiState.collectAsState()
    val musicState by musicViewModel.uiState.collectAsState()

    val userState by userViewModel.uiState.collectAsState()

    // TODO anders eventuell
    userViewModel.getUser()
    var text by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        /*AsyncImage(
           model = ImageRequest.Builder(LocalPlatformContext.current)
                .data("https://1.bp.blogspot.com/-m4g5Q9WZuLw/YO7FxYJsnsI/AAAAAAAA6fs/nyDiNA_6EHMrPw3qRLJ7FcR1-MoC4rkZwCLcBGAsYHQ/s0/javabeer.jpg")
               .build(),
            contentDescription = ""
        )
        Image(
            rememberAsyncImagePainter("https://1.bp.blogspot.com/-m4g5Q9WZuLw/YO7FxYJsnsI/AAAAAAAA6fs/nyDiNA_6EHMrPw3qRLJ7FcR1-MoC4rkZwCLcBGAsYHQ/s0/javabeer.jpg"), contentDescription = null
        )
        AsyncImage(
            modifier = Modifier.size(200.dp),
            model = "https://1.bp.blogspot.com/-m4g5Q9WZuLw/YO7FxYJsnsI/AAAAAAAA6fs/nyDiNA_6EHMrPw3qRLJ7FcR1-MoC4rkZwCLcBGAsYHQ/s0/javabeer.jpg",
            contentDescription = ""
        )*/

        LazyColumn(
            modifier = modifier
        ) {
            item { Text(userState.user?.name ?: "Nobody logged in") }

            item {
                Button(
                    onClick = { userViewModel.logoutUser() }
                ) {
                    Text("Logout")
                }
            }
            item {
                TextField(
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                    },
                    label = { Text("Search for movies") },
                )
            }

            item {
                Button(
                    onClick = {
                        movieViewModel.fetchMoviesByKeyword(text)
                    },
                ) {
                    Text("Search")
                }
            }

            items(movieState.movies) {
                if (it.title != null) {
                    Text(text = it.title, modifier = Modifier.clickable { movieViewModel.addSelectedMovieToList() })
                    Text(text = movieState.selectedMovie?.overview ?: "Get Details", modifier.clickable { movieViewModel.fetchMovieDetails(it.id) })
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/original${it.posterPath}",
                        contentDescription = "Poster"
                    )
                }
            }

            item { Text("MUSIC", style = MaterialTheme.typography.titleLarge) }
            item { if (musicState.artists.isNotEmpty()) Text("${musicState.artists.first()}") }
            item { Text("BOARD GAMES", style = MaterialTheme.typography.titleLarge) }
            item { if (bgState.boardGames.isNotEmpty()) Text("${bgState.boardGames.first()}") }
            item { Text("GAMES", style = MaterialTheme.typography.titleLarge) }
            item { if (gameState.games.isNotEmpty()) Text("${gameState.games.first()}") }
            item { Text("MOVIES", style = MaterialTheme.typography.titleLarge) }
            item { if (movieState.movies.isNotEmpty()) Text("${movieState.movies.first()}") }
            item { Text("SHOWS", style = MaterialTheme.typography.titleLarge) }
            item { if (showState.shows.isNotEmpty()) Text("${showState.shows.first()}") }
            item { Text("BOOKS", style = MaterialTheme.typography.titleLarge) }
            item { if (bookState.books.isNotEmpty()) Text("${bookState.books.first()}") }
        }
    }
}
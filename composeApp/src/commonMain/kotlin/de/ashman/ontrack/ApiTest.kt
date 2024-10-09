package de.ashman.ontrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import de.ashman.ontrack.login.ui.UserViewModel
import de.ashman.ontrack.media.boardgame.ui.BoardGameViewModel
import de.ashman.ontrack.media.book.ui.BookViewModel
import de.ashman.ontrack.media.movie.ui.MovieViewModel
import de.ashman.ontrack.media.album.ui.AlbumViewModel
import de.ashman.ontrack.media.show.ui.ShowViewModel
import de.ashman.ontrack.media.videogame.ui.VideoGameViewModel
import org.koin.compose.koinInject

@Composable
fun ApiTest(
    modifier: Modifier = Modifier,
    goToDetail: (String) -> Unit = {},
    movieViewModel: MovieViewModel = koinInject(),
    showViewModel: ShowViewModel = koinInject(),
    bookViewModel: BookViewModel = koinInject(),
    videoGameViewModel: VideoGameViewModel = koinInject(),
    boardGameViewModel: BoardGameViewModel = koinInject(),
    albumViewModel: AlbumViewModel = koinInject(),
    userViewModel: UserViewModel = koinInject(),
) {
    val movieState by movieViewModel.uiState.collectAsState()
    val showState by showViewModel.uiState.collectAsState()
    val bookState by bookViewModel.uiState.collectAsState()
    val videogameState by videoGameViewModel.uiState.collectAsState()
    val boardgameState by boardGameViewModel.uiState.collectAsState()
    val albumState by albumViewModel.uiState.collectAsState()

    val userState by userViewModel.uiState.collectAsState()

    // TODO anders eventuell
    userViewModel.getUser()
    var text by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
    ) {
        if (false) {
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
                    label = { Text("Search...") },
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
        }

        items(showState.shows) {
            Row {
                Text(text = it.name, modifier = Modifier.clickable { showViewModel.addToList(it) })
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = "Poster"
                )
            }
        }
        items(albumState.albums) {
            Row {
                Text(text = it.name, modifier = Modifier.clickable { albumViewModel.addToList(it) })
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = "Poster"
                )
            }
        }
        items(movieState.movies) {
            Row {
                Text(text = it.name, modifier = Modifier.clickable { movieViewModel.addToList(it) })
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = "Poster"
                )
            }
        }
        items(videogameState.videogames) {
            Row {
                Text(text = it.name, modifier = Modifier.clickable { videoGameViewModel.addToList(it) })
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = "Poster"
                )
            }
        }
        items(boardgameState.boardGames) {
            Row {
                Text(text = it.name, modifier = Modifier.clickable { boardGameViewModel.addToList(it) })
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = "Poster"
                )
            }
        }
        items(bookState.books) {
            Row {
                Text(text = it.name, modifier = Modifier.clickable { bookViewModel.addToList(it) })
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = "Poster"
                )
            }
        }
    }
}
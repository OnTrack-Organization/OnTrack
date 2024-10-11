package de.ashman.ontrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import de.ashman.ontrack.media.ui.StarRating
import de.ashman.ontrack.shelf.ui.BoardGameViewModel
import de.ashman.ontrack.shelf.ui.BookViewModel
import de.ashman.ontrack.shelf.ui.MovieViewModel
import de.ashman.ontrack.shelf.ui.AlbumViewModel
import de.ashman.ontrack.shelf.ui.ShowViewModel
import de.ashman.ontrack.shelf.ui.VideoGameViewModel
import org.koin.compose.koinInject

@Composable
fun ApiTest(
    modifier: Modifier = Modifier,
    goToMovieDetail: (String) -> Unit = {},
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
                        movieViewModel.fetchMediaByQuery(text)
                    },
                ) {
                    Text("Search")
                }
            }
        }

        if (bookState.isLoading) {
            item { CircularProgressIndicator() }
        } else if (bookState.errorMessage != null) {
            item { Text(text = "Error: ${bookState.errorMessage}") }
        } else {
            items(bookState.mediaList) {
                Row {
                    Text(
                        text = it.name,
                        modifier = Modifier.clickable { bookViewModel.addMediaToList(it) })
                    AsyncImage(
                        model = it.coverUrl,
                        contentDescription = "Book Cover"
                    )
                }
            }
        }
        items(movieState.mediaList) {
            Row {
                Text(
                    text = it.name,
                    modifier = Modifier.clickable { movieViewModel.addMediaToList(it) })
                AsyncImage(
                    modifier = modifier.clickable { goToMovieDetail(it.id) },
                    model = it.coverUrl,
                    contentDescription = "Poster",
                )
            }
        }
        items(showState.mediaList) {
            Row {
                Text(
                    text = it.name,
                    modifier = Modifier.clickable { showViewModel.addMediaToList(it) })
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = "Poster"
                )
            }
        }
        items(albumState.mediaList) {
            Row {
                Text(
                    text = it.name,
                    modifier = Modifier.clickable { albumViewModel.addMediaToList(it) })
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = "Poster"
                )
            }
        }
        items(videogameState.mediaList) {
            Row {
                Text(
                    text = it.name,
                    modifier = Modifier.clickable { videoGameViewModel.addMediaToList(it) })
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = "Poster"
                )
            }
        }
        items(boardgameState.mediaList) {
            Row {
                Text(
                    text = it.name,
                    modifier = Modifier.clickable { boardGameViewModel.addMediaToList(it) })
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = "Poster"
                )
            }
        }
    }
}
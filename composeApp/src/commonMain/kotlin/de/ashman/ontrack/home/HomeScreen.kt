package de.ashman.ontrack.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import de.ashman.ontrack.ApiTest
import de.ashman.ontrack.media.book.ui.BookViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    goToDetail: (String) -> Unit,
    bookViewModel: BookViewModel = koinInject(),
) {
    ApiTest(
        modifier = modifier.padding(16.dp),
        goToDetail = goToDetail,
    )
    /* Column(modifier.clickable { goToDetail(1234) }) {
         Text("Home")
     }*/
    /*val state by bookViewModel.uiState.collectAsState()

    LazyColumn {
        items(state.books) {
            if (it.title != null) Text(it.title)
            AsyncImage(
                model = it.coverUrl,
                contentDescription = "Poster"
            )
        }
    }*/
}

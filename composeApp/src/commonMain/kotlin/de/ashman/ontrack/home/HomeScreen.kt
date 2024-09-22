package de.ashman.ontrack.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import de.ashman.ontrack.ApiTest
import de.ashman.ontrack.OnTrackScreen
import de.ashman.ontrack.navigation.Route

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    goToDetail: (Int) -> Unit,
) {
    ApiTest(
        modifier = modifier,
        goToDetail = goToDetail,
    )
    /*Column(modifier.clickable { goToDetail(1234) }) {
        Text("Home")
    }*/
}

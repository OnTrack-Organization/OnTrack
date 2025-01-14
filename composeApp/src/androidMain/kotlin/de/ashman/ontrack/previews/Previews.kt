package de.ashman.ontrack.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.ashman.ontrack.media.model.Movie
import de.ashman.ontrack.media.model.ConsumeStatus

@Composable
@Preview
fun MovieDetailsPreview() {
    val movie = Movie(
        id = "1",
        name = "Lion",
        consumeStatus = ConsumeStatus.WATCHED,
        userRating = 5f,
        coverUrl = "https://image.tmdb.org/t/p/original/iBGRbLvg6kVc7wbS8wDdVHq6otm.jpg",
        backdropPath = "",
        genres = listOf("Drama"),
        originCountry = listOf("Japan"),
        originalLanguage = "JP",
        originalTitle = "Raion",
        description = "A very cool film about a lion. Rawr.",
        popularity = 20.0,
        releaseYear = "23/01/2023",
        revenue = 5000L,
        runtime = 100,
        status = "Filmed",
        voteAverage = 99.0,
        voteCount = 1000,
    )
}
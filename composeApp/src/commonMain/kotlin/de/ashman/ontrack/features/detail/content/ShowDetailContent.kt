package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.api.utils.getLivingDates
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Season
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.features.common.CreatorCard
import de.ashman.ontrack.features.common.MediaChips
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.common.SMALL_POSTER_HEIGHT
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_director
import ontrack.composeapp.generated.resources.detail_genres
import ontrack.composeapp.generated.resources.detail_seasons
import ontrack.composeapp.generated.resources.detail_similar_shows
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.ShowDetailContent(
    show: Show,
    onClickItem: (Media) -> Unit = { },
) {
    item {
        CreatorCard(
            title = Res.string.detail_director,
            name = show.director?.name,
            subInfo = getLivingDates(show.director?.birthDate, show.director?.deathDate),
            imageUrl = show.director?.imageUrl,
            description = show.director?.bio,
        )
    }
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = show.description,
        )
    }
    item {
        MediaChips(
            title = stringResource(Res.string.detail_genres),
            items = show.genres,
        )
    }
    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_similar_shows),
            items = show.similarShows,
            onClickItem = onClickItem,
        )
    }
    item {
        show.numberOfSeasons?.let {
            SeasonsRow(
                title = pluralStringResource(Res.plurals.detail_seasons, show.numberOfSeasons, show.numberOfSeasons),
                seasons = show.seasons,
            )
        }
    }
}

@Composable
fun SeasonsRow(
    title: String,
    seasons: List<Season>?,
) {
    seasons?.let {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(seasons) {
                    MediaPoster(
                        modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                        title = it.title,
                        coverUrl = it.coverUrl,
                        textStyle = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
    }
}
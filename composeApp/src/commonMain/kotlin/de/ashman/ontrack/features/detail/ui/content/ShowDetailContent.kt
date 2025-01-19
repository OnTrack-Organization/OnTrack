package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaGenres
import de.ashman.ontrack.features.detail.ui.MediaRow
import de.ashman.ontrack.domain.Show
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_similar_shows
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.ShowDetailContent(
    show: Show,
    onClickItem: (String) -> Unit = { },
) {
    item {
        MediaDescription(show.description)
    }
    item {
        MediaGenres(show.genres)
    }
    item {
        MediaRow(
            title = stringResource(Res.string.detail_similar_shows),
            otherMedia = show.similarShows,
            onClickItem = onClickItem,
        )
    }
}
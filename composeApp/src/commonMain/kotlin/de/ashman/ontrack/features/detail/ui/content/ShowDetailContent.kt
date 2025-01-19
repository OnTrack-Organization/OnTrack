package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaGenres
import de.ashman.ontrack.features.detail.ui.MediaRow
import de.ashman.ontrack.domain.Show
import de.ashman.ontrack.domain.sub.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_similar
import org.jetbrains.compose.resources.pluralStringResource
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
            title = stringResource(Res.string.detail_similar, pluralStringResource(getMediaTypeUi(show.mediaType).title, 2)),
            otherMedia = show.similarShows,
            onClickItem = onClickItem,
        )
    }
}
package de.ashman.ontrack.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.media.model.ConsumeStatus
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShelfScreen(
    modifier: Modifier = Modifier,
    goToShelf: (MediaType) -> Unit = {},
    // TODO add ShelfViewModel
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MediaType.entries.forEach { mediaType ->
            val counts = emptyMap<ConsumeStatus, Int>()

            MediaCard(
                modifier = Modifier.weight(1f),
                mediaType = mediaType,
                counts = counts,
                goToShelf = goToShelf,
            )
        }
    }
}

@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    mediaType: MediaType,
    counts: Map<ConsumeStatus, Int>,
    goToShelf: (MediaType) -> Unit,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { goToShelf(mediaType) }
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MediaTitle(mediaType.title)

                if (mediaType.consumeStatuses.isNotEmpty()) {
                    MediaCounts(
                        consumeStatuses = mediaType.consumeStatuses,
                        counts = counts,
                    )
                }
            }
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = mediaType.icon(),
                contentDescription = stringResource(mediaType.iconDescription)
            )
        }
    }
}

@Composable
private fun MediaCounts(
    consumeStatuses: List<ConsumeStatus>,
    counts: Map<ConsumeStatus, Int>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        consumeStatuses.forEach {
            MediaCount(
                count = counts[it] ?: 0,
                consumeStatus = it,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MediaCount(
    count: Int,
    consumeStatus: ConsumeStatus,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = consumeStatus.getConsumeStatusIcon(),
            contentDescription = consumeStatus.name
        )
        Text("$count")
    }
}

@Composable
private fun MediaTitle(mediaTitle: StringResource) {
    Text(
        text = stringResource(mediaTitle),
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
    )
}

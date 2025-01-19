package de.ashman.ontrack.features.shelf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.ashman.ontrack.domain.MediaType

@Composable
fun ShelfScreen(
    modifier: Modifier = Modifier,
    goToShelf: (MediaType) -> Unit = {},
    // TODO add ShelfViewModel
) {
    /*Column(
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
    }*/
}

/*@Composable
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
                contentDescription = stringResource(mediaType.title)
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
}*/

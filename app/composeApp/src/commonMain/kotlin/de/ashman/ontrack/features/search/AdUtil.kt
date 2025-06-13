package de.ashman.ontrack.features.search

import de.ashman.ontrack.domain.media.Media

sealed class SearchItem {
    data class MediaItem(val media: Media) : SearchItem()
    data class AdItem(val index: Int) : SearchItem()
}

fun interleaveAds(
    items: List<Media>,
    adFrequency: Int = 6,
    showAds: Boolean = false,
): List<SearchItem> {
    if (!showAds) return items.map { SearchItem.MediaItem(it) }

    val result = mutableListOf<SearchItem>()
    items.forEachIndexed { index, item ->
        result.add(SearchItem.MediaItem(item))
        if ((index + 1) % adFrequency == 0) {
            result.add(SearchItem.AdItem(index = index))
        }
    }
    return result
}

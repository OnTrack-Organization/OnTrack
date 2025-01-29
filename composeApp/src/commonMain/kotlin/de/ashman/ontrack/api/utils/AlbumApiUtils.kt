package de.ashman.ontrack.api.utils

import de.ashman.ontrack.api.album.dto.TrackDto

fun List<TrackDto>.toNumberedTracks(): String = joinToString(separator = "\n") { "${it.trackNumber}. ${it.name}" }
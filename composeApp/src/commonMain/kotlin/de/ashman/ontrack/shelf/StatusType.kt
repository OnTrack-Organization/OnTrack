package de.ashman.ontrack.shelf

import kotlinx.serialization.Serializable

@Serializable
enum class StatusType {
    ALL, BINGING, PLAYING, READING, WATCHED, BINGED, READ, PLAYED, DROPPED, CATALOG
}
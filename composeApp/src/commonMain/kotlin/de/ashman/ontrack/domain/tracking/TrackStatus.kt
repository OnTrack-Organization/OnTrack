package de.ashman.ontrack.domain.tracking

import kotlinx.serialization.Serializable

@Serializable
enum class TrackStatus {
    CATALOG, CONSUMING, CONSUMED, DROPPED
}
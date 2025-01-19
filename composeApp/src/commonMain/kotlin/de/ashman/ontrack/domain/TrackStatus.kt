package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@CommonParcelize
@Serializable
data class TrackStatus(
    val id: String = Uuid.random().toString(),
    val timestamp: Long? = null,
    val statusType: TrackStatusType? = null,
    val rating: Int? = null,
    val review: String? = null,
) : CommonParcelable

enum class TrackStatusType {
    CONSUMING, CONSUMED, DROPPED, CATALOG
}
package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
data class TrackingComment(
    val id: String,
    val userId: String,
    val userImageUrl: String,
    val username: String,
    val comment: String,
    val timestamp: Long = System.now().toEpochMilliseconds(),
) : CommonParcelable

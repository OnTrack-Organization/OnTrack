package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.db.entity.tracking.LikeEntity
import de.ashman.ontrack.db.entity.user.UserData
import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@CommonParcelize
@Serializable
data class Like(
    @OptIn(ExperimentalUuidApi::class)
    val id: String = Uuid.random().toString(),
    val userData: UserData,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
) : CommonParcelable {
    fun toEntity() = LikeEntity(
        id = id,
        userData = userData,
        timestamp = timestamp,
    )
}
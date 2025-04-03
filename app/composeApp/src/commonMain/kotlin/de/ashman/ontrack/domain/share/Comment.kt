package de.ashman.ontrack.domain.share

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@CommonParcelize
@Serializable
data class Comment(
    @OptIn(ExperimentalUuidApi::class)
    val id: String = Uuid.random().toString(),
    val userId: String,
    val name: String,
    val username: String,
    val userImageUrl: String,
    val comment: String,
    val timestamp: Long = System.now().toEpochMilliseconds(),
) : CommonParcelable

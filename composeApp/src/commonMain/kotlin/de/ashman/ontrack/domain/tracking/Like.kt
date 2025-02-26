package de.ashman.ontrack.domain.tracking

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@CommonParcelize
@Serializable
data class Like(
    @OptIn(ExperimentalUuidApi::class)
    val id: String = Uuid.random().toString(),
    val userId: String = Firebase.auth.currentUser!!.uid,
    val timestamp: Long = System.now().toEpochMilliseconds(),
) : CommonParcelable
package de.ashman.ontrack.domain.feed

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
data class Comment(
    @OptIn(ExperimentalUuidApi::class)
    val id: String = Uuid.random().toString(),
    val userId: String = Firebase.auth.currentUser?.uid.orEmpty(),
    val username: String = Firebase.auth.currentUser?.displayName.orEmpty(),
    val userImageUrl: String = Firebase.auth.currentUser?.photoURL.orEmpty(),
    val comment: String,
    val timestamp: Long = System.now().toEpochMilliseconds(),
) : CommonParcelable

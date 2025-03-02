package de.ashman.ontrack.domain.feed

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
data class Like(
    val userId: String = Firebase.auth.currentUser?.uid.orEmpty(),
    val username: String = Firebase.auth.currentUser?.displayName.orEmpty(),
    val userImageUrl: String = Firebase.auth.currentUser?.photoURL.orEmpty(),
) : CommonParcelable
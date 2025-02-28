package de.ashman.ontrack.domain.user

import de.ashman.ontrack.db.entity.user.UserData
import de.ashman.ontrack.db.entity.user.UserEntity
import dev.gitlive.firebase.auth.FirebaseUser

data class User(
    val userData: UserData,
    val email: String,
    val fcmToken: String?,
) {
    fun toEntity() = UserEntity(
        userData = userData,
        fcmToken = fcmToken.orEmpty(),
        email = email,
    )
}

fun FirebaseUser.toDomain() =
    User(
        userData = UserData(
            id = uid,
            name = displayName.orEmpty(),
            username = displayName?.replace(" ", "")?.lowercase().orEmpty(),
            imageUrl = photoURL.orEmpty(),
        ),
        fcmToken = null,
        email = email.orEmpty(),
    )
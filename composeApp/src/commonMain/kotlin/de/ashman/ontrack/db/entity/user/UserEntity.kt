package de.ashman.ontrack.db.entity.user

import de.ashman.ontrack.domain.user.User
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val userData: UserData,
    val email: String,
    val fcmToken: String,
    val friends: List<String> = listOf(),
    val updatedAt: Timestamp = Timestamp.now(),
) {
    fun toDomain(): User = User(
        userData = userData,
        fcmToken = fcmToken,
        email = email,
    )
}
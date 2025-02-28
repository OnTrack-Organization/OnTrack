package de.ashman.ontrack.domain.user

import de.ashman.ontrack.db.entity.user.UserData
import kotlinx.serialization.Serializable

@Serializable
data class Friend(
    val userData: UserData,
)

package de.ashman.ontrack.domain.feed

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
data class Like(
    val userId: String,
    val name: String,
    val username: String,
    val userImageUrl: String,
) : CommonParcelable
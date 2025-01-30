package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
data class RatingStats(
    val totalAppRatings: Int? = null,
    val averageAppRating: Double? = null,

    val totalApiRatings: Int? = null,
    val averageApiRating: Double? = null,
) : CommonParcelable

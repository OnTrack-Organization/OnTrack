package de.ashman.ontrack.network.services.share.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentDto(
    val message: String
)

package de.ashman.ontrack.api.boardgame.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardgameImagesDto(
    val images: List<BoardgameImageDto>
)

@Serializable
data class BoardgameImageDto(
    val imageid: String,
    @SerialName("imageurl_lg")
    val imageUrlLarge: String,
    val caption: String? = null,
    val numrecommend: String? = null,
    val numcomments: String? = null,
    val user: BoardgameImageUserDto,
    @SerialName("imageurl")
    val imageUrlThumb: String,
    val href: String
)

@Serializable
data class BoardgameImageUserDto(
    val username: String,
    val avatar: String,
    val avatarfile: String
)

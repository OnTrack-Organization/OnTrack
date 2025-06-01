package de.ashman.ontrack.feature.share.controller.dto

import org.springframework.data.domain.Page

data class LikesDto(
    val likes: Page<LikeDto>,
    val likeCount: Int,
)

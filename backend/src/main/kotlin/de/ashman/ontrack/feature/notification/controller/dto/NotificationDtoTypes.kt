package de.ashman.ontrack.feature.notification.controller.dto

import de.ashman.ontrack.feature.user.controller.dto.UserDto
import java.util.*

data class FriendRequestReceivedDto(
    override val id: UUID,
    override val sender: UserDto,
    override val read: Boolean,
    override val timestamp: Long,
) : NotificationDto()

data class FriendRequestAcceptedDto(
    override val id: UUID,
    override val sender: UserDto,
    override val read: Boolean,
    override val timestamp: Long,
) : NotificationDto()

data class RecommendationReceivedDto(
    override val id: UUID,
    override val sender: UserDto,
    override val read: Boolean,
    override val timestamp: Long,
    val recommendation: SimpleRecommendationDto,
) : NotificationDto()

data class PostLikedDto(
    override val id: UUID,
    override val sender: UserDto,
    override val read: Boolean,
    override val timestamp: Long,
    val post: SimplePostDto,
) : NotificationDto()

data class PostCommentedDto(
    override val id: UUID,
    override val sender: UserDto,
    override val read: Boolean,
    override val timestamp: Long,
    val post: SimplePostDto,
    val comment: SimpleCommentDto,
) : NotificationDto()

data class MentionedDto(
    override val id: UUID,
    override val sender: UserDto,
    override val read: Boolean,
    override val timestamp: Long,
    val post: SimplePostDto,
    val comment: SimpleCommentDto,
) : NotificationDto()

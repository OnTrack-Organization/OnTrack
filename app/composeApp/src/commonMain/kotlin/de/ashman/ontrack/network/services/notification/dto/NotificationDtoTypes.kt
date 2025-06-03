package de.ashman.ontrack.network.services.notification.dto

import de.ashman.ontrack.network.services.account.dto.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("FriendRequestReceivedDto")
data class FriendRequestReceivedDto(
    override val id: String,
    override val sender: UserDto,
    override val read: Boolean,
    override val createdAt: Long
) : NotificationDto()

@Serializable
@SerialName("FriendRequestAcceptedDto")
data class FriendRequestAcceptedDto(
    override val id: String,
    override val sender: UserDto,
    override val read: Boolean,
    override val createdAt: Long
) : NotificationDto()

@Serializable
@SerialName("RecommendationReceivedDto")
data class RecommendationReceivedDto(
    override val id: String,
    override val sender: UserDto,
    override val read: Boolean,
    override val createdAt: Long,
    val recommendation: SimpleRecommendationDto,
) : NotificationDto()

@Serializable
@SerialName("PostLikedDto")
data class PostLikedDto(
    override val id: String,
    override val sender: UserDto,
    override val read: Boolean,
    override val createdAt: Long,
    val post: SimplePostDto,
) : NotificationDto()

@Serializable
@SerialName("PostCommentedDto")
data class PostCommentedDto(
    override val id: String,
    override val sender: UserDto,
    override val read: Boolean,
    override val createdAt: Long,
    val post: SimplePostDto,
    val comment: SimpleCommentDto,
) : NotificationDto()

@Serializable
@SerialName("MentionedDto")
data class MentionedDto(
    override val id: String,
    override val sender: UserDto,
    override val read: Boolean,
    override val createdAt: Long,
    val post: SimplePostDto,
    val comment: SimpleCommentDto,
) : NotificationDto()

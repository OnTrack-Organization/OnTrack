package de.ashman.ontrack.domain.user

import de.ashman.ontrack.entity.user.FriendEntity
import de.ashman.ontrack.entity.user.FriendRequestEntity

fun FriendEntity.toDomain() =
    Friend(
        id = id,
        username = username,
        name = name,
        imageUrl = imageUrl,
    )

fun Friend.toEntity() =
    FriendEntity(
        id = id,
        username = username,
        name = name,
        imageUrl = imageUrl,
    )

fun FriendRequestEntity.toDomain() =
    FriendRequest(
        userId = userId,
        username = username,
        name = name,
        imageUrl = imageUrl,
        status = status,
    )

fun FriendRequest.toEntity() =
    FriendRequestEntity(
        userId = userId,
        username = username,
        name = name,
        imageUrl = imageUrl,
        status = status,
    )
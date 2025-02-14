package de.ashman.ontrack.domain.user

import de.ashman.ontrack.db.entity.FriendEntity
import de.ashman.ontrack.db.entity.FriendRequestEntity
import de.ashman.ontrack.db.entity.UserEntity
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toEntity() =
    UserEntity(
        id = uid,
        email = email,
        displayName = displayName,
        // TODO different way
        username = "@${displayName?.lowercase()}",
        imageUrl = photoURL,
    )

fun UserEntity.toDomain() =
    User(
        id = id,
        email = email,
        name = displayName,
        username = username,
        imageUrl = imageUrl,
        friends = friends.map { it.toDomain() },
        receivedRequests = receivedRequests.map { it.toDomain() },
        sentRequests = sentRequests.map { it.toDomain() },
        //trackings = currentTrackings,
    )

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
        id = id,
        senderId = senderId,
        senderUsername = senderUsername,
        senderName = senderName,
        senderImageUrl = senderImageUrl,
        status = status,
    )

fun FriendRequest.toEntity() =
    FriendRequestEntity(
        id = id,
        senderId = senderId,
        senderUsername = senderUsername,
        senderName = senderName,
        senderImageUrl = senderImageUrl,
        status = status,
    )
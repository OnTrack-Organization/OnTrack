package de.ashman.ontrack.user.domain.repository

class FriendRequestPendingException
    : RuntimeException("A pending friend request already exists")

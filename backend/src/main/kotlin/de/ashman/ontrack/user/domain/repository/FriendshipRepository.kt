package de.ashman.ontrack.user.domain.repository

interface FriendshipRepository {
    fun getFriendIds(userId: String): List<String>
    fun beginFriendship(userId1: String, userId2: String)
    fun endFriendship(userId1: String, userId2: String)
    fun areFriends(userId1: String, userId2: String): Boolean
}

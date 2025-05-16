package de.ashman.ontrack.user.domain.repository

import de.ashman.ontrack.user.domain.model.FriendRequest

interface FriendRequestRepository {
    fun findBySenderAndReceiver(sender: String, receiver: String): FriendRequest?

    /**
     * @throws FriendRequestPendingException
     */
    fun save(request: FriendRequest)

    /**
     * @return Ids of all users who sent a friend request to the sender.
     *         Only pending requests are considered.
     */
    fun findSendersOfReceivedRequests(receiver: String): List<String>

    /**
     * @return Ids of all users who received a friend request by the sender
     *         Only pending requests are considered.
     */
    fun findReceiversOfSentRequests(sender: String): List<String>
}

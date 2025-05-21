package de.ashman.ontrack.feature.friend.domain.exception

import java.lang.RuntimeException

class FriendRequestAlreadyProcessedException : RuntimeException("Friend request outdated")

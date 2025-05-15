package de.ashman.ontrack.user.domain.model

import java.lang.RuntimeException

class FriendRequestAlreadyProcessedException
    : RuntimeException("Friend request outdated")
